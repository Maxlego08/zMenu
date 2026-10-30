package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.dto.InventoryDTO;
import fr.maxlego08.menu.api.storage.spi.InventoryOpenRecord;
import fr.maxlego08.menu.api.storage.spi.PlayerDataEntry;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.TypeSafeCache;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * zMenu's storage, on top of the {@link StorageProvider} chosen by {@code storage-type}.
 *
 * <p>Player data values and the opened-inventory log are buffered here and written in batches
 * every {@code batch-task} seconds; everything else goes straight to the provider, which applies
 * writes in the order they are made.</p>
 */
public class ZStorageManager implements StorageManager {

    private static final long FLUSH_TIMEOUT_SECONDS = 30;
    private static final long PURGE_INTERVAL_MINUTES = 10;

    private final MenuPlugin plugin;
    private final TypeSafeCache cache = new TypeSafeCache();

    /**
     * Player data values waiting for the next batch, by player and key. A newer value replaces the
     * one waiting for the same key.
     */
    private final Map<PendingKey, PlayerDataEntry> pendingData = new ConcurrentHashMap<>();
    private final AtomicBoolean flushing = new AtomicBoolean(false);
    private StorageProvider provider;

    public ZStorageManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The key of the provider a {@code storage-type} value selects: the SQL provider for
     * {@code SQLITE}, {@code MYSQL} and {@code MARIADB}, the memory provider for {@code NONE}, and
     * otherwise the key of a provider an addon registers, such as {@code myaddon:redis}.
     */
    static @Nullable Key providerKey(String storageType) {
        if (SqlStorageProvider.databaseType(storageType) != null) return ZMenuKeys.key("sql");
        if (storageType.equalsIgnoreCase("NONE")) return ZMenuKeys.key("memory");
        return ZMenuKeys.tryParse(storageType.toLowerCase(Locale.ROOT));
    }

    @Override
    public void loadDatabase() {

        WritableRegistry<StorageProvider> providers = this.plugin.getInventoryManager().getRegistries().writable(Registries.STORAGE_PROVIDER);
        providers.register(this.plugin, new SqlStorageProvider());
        providers.register(this.plugin, new MemoryStorageProvider());

        StorageConfiguration configuration = StorageConfiguration.read(this.plugin.getConfig());
        Key key;
        if (this.plugin.isMockBukkitServer()) {
            Logger.info("You are using a MockBukkit server, data is kept in memory.");
            key = ZMenuKeys.key("memory");
        } else {
            key = providerKey(configuration.storageType());
        }

        KeyedRegistry<StorageProvider> registry = this.plugin.getInventoryManager().getRegistries().registry(Registries.STORAGE_PROVIDER);
        Optional<StorageProvider> optional = key == null ? Optional.empty() : registry.get(key);
        if (optional.isEmpty()) {
            Logger.info("Unknown storage-type " + configuration.storageType() + ". Use SQLITE, MYSQL, MARIADB, NONE or the key of a storage registered by an addon.", Logger.LogType.ERROR);
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }

        StorageProvider candidate = optional.get();
        try {
            candidate.open(new StorageContext(this.plugin, this.plugin.getConfig(), this.plugin.getDataFolder(), this.plugin.getLogger())).join();
        } catch (Exception exception) {
            Logger.error("Unable to connect to database !", exception);
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }

        this.provider = candidate;
        if (key.equals(ZMenuKeys.key("memory")) && !this.plugin.isMockBukkitServer()) {
            Logger.info("You are not using a database, data is kept in memory until the server stops.");
        }

        this.startBatchTask(configuration.batchSeconds());
        this.plugin.getScheduler().runTimerAsync(this::purgeExpiredData, 1, PURGE_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * Deletes expired player data from the database. Expired values used to stay there until
     * someone read them.
     */
    private void purgeExpiredData() {
        if (!this.isEnable()) return;
        this.logFailure(this.provider.playerData().deleteExpired(System.currentTimeMillis()), "delete the expired player data");
    }

    /**
     * Loads the values of one player, with the values still waiting to be written laid over them.
     */
    public CompletableFuture<List<PlayerDataEntry>> loadPlayerData(@NonNull UUID playerId) {
        if (!this.isEnable()) return CompletableFuture.completedFuture(List.of());

        return this.provider.playerData().load(playerId).thenApply(stored -> {
            Map<String, PlayerDataEntry> entries = new LinkedHashMap<>();
            stored.forEach(entry -> entries.put(entry.key(), entry));
            this.pendingData.forEach((key, entry) -> {
                if (key.playerId().equals(playerId)) entries.put(key.key(), entry);
            });
            return new ArrayList<>(entries.values());
        });
    }

    /**
     * @return every key used by at least one player, including the ones still waiting to be written
     */
    public CompletableFuture<Set<String>> loadDataKeys() {
        if (!this.isEnable()) return CompletableFuture.completedFuture(Set.of());

        return this.provider.playerData().keys().thenApply(stored -> {
            Set<String> keys = new HashSet<>(stored);
            this.pendingData.keySet().forEach(key -> keys.add(key.key()));
            return keys;
        });
    }

    public CompletableFuture<Optional<String>> loadSavedInventory(@NonNull UUID playerId) {
        if (!this.isEnable()) return CompletableFuture.completedFuture(Optional.empty());

        return this.provider.savedInventories().load(playerId);
    }

    /**
     * @return whether a value of this player still waits to be written
     */
    public boolean hasPendingData(@NonNull UUID playerId) {
        for (PendingKey key : this.pendingData.keySet()) {
            if (key.playerId().equals(playerId)) return true;
        }
        return false;
    }

    /**
     * Removes the waiting values matching a filter. A value replaced concurrently is kept.
     */
    private List<PlayerDataEntry> drainPendingData(java.util.function.Predicate<PendingKey> filter) {
        List<PlayerDataEntry> drained = new ArrayList<>();
        for (Map.Entry<PendingKey, PlayerDataEntry> entry : this.pendingData.entrySet()) {
            if (filter.test(entry.getKey()) && this.pendingData.remove(entry.getKey(), entry.getValue())) {
                drained.add(entry.getValue());
            }
        }
        return drained;
    }

    private record PendingKey(UUID playerId, String key) {
    }

    /**
     * @return the provider in use, or null when the storage could not be opened
     */
    public @Nullable StorageProvider getProvider() {
        return this.provider;
    }

    private void startBatchTask(int seconds) {

        if (seconds <= 0) return;

        this.plugin.getScheduler().runTimerAsync(this::storeBuffered, seconds, seconds, TimeUnit.SECONDS);
    }

    /**
     * Hands the buffered values and log entries to the provider.
     *
     * @return completes once they are stored
     */
    private CompletableFuture<Void> storeBuffered() {
        if (!this.isEnable()) return CompletableFuture.completedFuture(null);

        List<CompletableFuture<Void>> writes = new ArrayList<>();

        List<InventoryOpenRecord> records = this.cache.drain(InventoryOpenRecord.class);
        if (!records.isEmpty()) {
            this.provider.inventoryLogs().ifPresent(store -> writes.add(this.logFailure(store.append(records), "store the opened inventories")));
        }

        List<PlayerDataEntry> entries = this.drainPendingData(key -> true);
        if (!entries.isEmpty()) {
            writes.add(this.logFailure(this.provider.playerData().save(entries), "store the player data"));
        }

        return CompletableFuture.allOf(writes.toArray(new CompletableFuture[0]));
    }

    private <T> CompletableFuture<T> logFailure(CompletableFuture<T> future, String action) {
        return future.whenComplete((result, throwable) -> {
            if (throwable != null) Logger.error("Unable to " + action, throwable);
        });
    }

    private <T> T await(Supplier<CompletableFuture<T>> read, T fallback, String action) {
        try {
            return read.get().join();
        } catch (Exception exception) {
            Logger.error("Unable to " + action, exception);
            return fallback;
        }
    }

    /**
     * Writes everything still buffered and waits until it is stored.
     *
     * <p>Deliberately blocking. This is called from {@code onDisable}, where the plugin scheduler
     * is being torn down, so the server must not stop before the data is written.</p>
     */
    @Override
    public void flush() {

        if (!this.isEnable()) return;

        try {
            this.storeBuffered().get(FLUSH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException exception) {
            Logger.error("Unable to write the buffered data to the database", exception);
        }
    }

    /**
     * Lets the provider store what is still queued and release the database. Called once, when
     * the plugin disables.
     */
    public void close() {

        if (this.provider == null) return;

        try {
            this.provider.close().get(FLUSH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException exception) {
            Logger.error("Some database writes did not finish before the server stopped", exception);
        }
    }

    /**
     * Persists buffered records when a player leaves.
     *
     * <p>Not needed to stop a player rolling their own data back, the data manager keeps it in
     * memory for the whole uptime, but it shrinks what an outright crash can lose from up to one
     * full batch interval of everybody's data down to whatever arrived since the last disconnect.</p>
     *
     * <p>Several players leaving at once coalesce into a single flush rather than one database
     * round trip each.</p>
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        if (!this.isEnable() || !Configuration.flushStorageOnQuit) return;

        if (!this.flushing.compareAndSet(false, true)) return;

        this.storeBuffered().whenComplete((result, throwable) -> this.flushing.set(false));
    }

    @Override
    public boolean isEnable() {
        return this.provider != null;
    }

    @Override
    public void upsertData(@NonNull UUID uuid, @NonNull Data data) {
        if (!this.isEnable()) return;

        Object value = data.getValue();
        this.pendingData.put(new PendingKey(uuid, data.getKey()), new PlayerDataEntry(uuid, data.getKey(), value == null ? "" : value.toString(), data.getExpiredAt()));
    }

    @Override
    public void clearData() {
        if (!this.isEnable()) return;

        this.pendingData.clear();
        this.logFailure(this.provider.playerData().deleteAll(), "delete the player data");
    }

    @Override
    public void clearData(@NonNull UUID uniqueId) {
        if (!this.isEnable()) return;

        this.drainPendingData(key -> key.playerId().equals(uniqueId));
        this.logFailure(this.provider.playerData().deletePlayer(uniqueId), "delete the player data of " + uniqueId);
    }

    @Override
    public void clearData(@NonNull String key) {
        if (!this.isEnable()) return;

        this.drainPendingData(pending -> pending.key().equals(key));
        this.logFailure(this.provider.playerData().deleteKey(key), "delete the player data key " + key);
    }

    @Override
    public void removeData(@NonNull UUID uuid, @NonNull String key) {
        if (!this.isEnable()) return;

        this.pendingData.remove(new PendingKey(uuid, key));
        this.logFailure(this.provider.playerData().delete(uuid, key), "delete the player data key " + key + " of " + uuid);
    }

    @Override
    public @NonNull List<DataDTO> loadPlayers() {
        if (!this.isEnable()) return List.of();

        List<PlayerDataEntry> entries = this.await(() -> this.provider.playerData().loadAll(), List.of(), "load the player data");
        List<DataDTO> dataList = new ArrayList<>(entries.size());
        for (PlayerDataEntry entry : entries) {
            dataList.add(new DataDTO(entry.playerId(), entry.key(), entry.value(), entry.expiresAt() == 0 ? null : new Date(entry.expiresAt())));
        }
        return dataList;
    }

    @Override
    public @NonNull List<InventoryDTO> loadInventories() {
        if (!this.isEnable()) return List.of();

        Map<UUID, String> inventories = this.await(() -> this.provider.savedInventories().loadAll(), Map.of(), "load the saved inventories");
        List<InventoryDTO> dtos = new ArrayList<>(inventories.size());
        inventories.forEach((playerId, inventory) -> dtos.add(new InventoryDTO(playerId, inventory)));
        return dtos;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerOpenInventory(PlayerOpenInventoryEvent event) {

        if (!this.isEnable() || !Configuration.enablePlayerOpenInventoryLogs) return;

        List<String> previousInventories = new ArrayList<>();
        for (Inventory oldInventory : event.getOldInventories()) {
            if (oldInventory != null) previousInventories.add(oldInventory.getFileName());
        }
        this.cache.add(new InventoryOpenRecord(event.getPlayer().getUniqueId(), event.getInventory().getPlugin().getName(),
                event.getInventory().getFileName(), event.getPage(), previousInventories));
    }

    @Override
    public void removeInventory(@NonNull UUID uuid) {
        if (!this.isEnable()) return;

        this.logFailure(this.provider.savedInventories().delete(uuid), "delete the saved inventory of " + uuid);
    }

    @Override
    public @NonNull Set<String> getVisitedWorlds(@NonNull UUID playerId) {
        if (!this.isEnable()) return Set.of();

        return this.await(() -> this.provider.worldVisits().load(playerId), Set.of(), "load the visited worlds of " + playerId);
    }

    @Override
    public void markWorldVisited(@NonNull UUID playerId, @NonNull String worldName) {
        if (!this.isEnable()) return;

        this.logFailure(this.provider.worldVisits().markVisited(playerId, worldName), "store a visited world of " + playerId);
    }

    @Override
    public @NonNull Map<String, Long> loadPlayedAnimations(@NonNull UUID playerId) {
        if (!this.isEnable()) return Map.of();

        return this.await(() -> this.provider.animations().load(playerId), Map.of(), "load the played animations of " + playerId);
    }

    @Override
    public void markAnimationPlayed(@NonNull UUID playerId, @NonNull String animationKey, long playedAt) {
        if (!this.isEnable()) return;

        this.logFailure(this.provider.animations().markPlayed(playerId, animationKey, playedAt), "store a played animation of " + playerId);
    }

    @Override
    public void clearPlayedAnimations(@NonNull UUID playerId, String animationKey) {
        if (!this.isEnable()) return;

        this.logFailure(this.provider.animations().clear(playerId, animationKey), "forget the played animations of " + playerId);
    }

    @Override
    public void storeInventory(@NonNull UUID uuid, @NonNull InventoryPlayer inventoryPlayer) {
        if (!this.isEnable()) return;

        this.logFailure(this.provider.savedInventories().save(uuid, inventoryPlayer.toInventoryString()), "store the saved inventory of " + uuid);
    }
}
