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
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import fr.maxlego08.menu.storage.redis.RedisMessaging;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import fr.maxlego08.menu.variables.ZVariableService;
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

    private final MenuPlugin plugin;
    private final TypeSafeCache cache = new TypeSafeCache();
    private final AtomicBoolean flushing = new AtomicBoolean(false);
    private StorageProvider provider;
    private ZVariableService variableService;
    private RedisMessaging redisMessaging;

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
        if (storageType.equalsIgnoreCase("REDIS")) return ZMenuKeys.key("redis");
        return ZMenuKeys.tryParse(storageType.toLowerCase(Locale.ROOT));
    }

    @Override
    public void loadDatabase() {

        WritableRegistry<StorageProvider> providers = this.plugin.getInventoryManager().getRegistries().writable(Registries.STORAGE_PROVIDER);
        providers.register(this.plugin, new SqlStorageProvider());
        providers.register(this.plugin, new MemoryStorageProvider());
        providers.register(this.plugin, new RedisStorageProvider());

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
        String serverId;
        try {
            serverId = configuration.resolveServerId(this.plugin.getDataFolder());
            candidate.open(new StorageContext(this.plugin, this.plugin.getConfig(), this.plugin.getDataFolder(), this.plugin.getLogger(), serverId)).join();
        } catch (Exception exception) {
            Logger.error("Unable to connect to database !", exception);
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            return;
        }

        this.provider = candidate;
        if (key.equals(ZMenuKeys.key("memory")) && !this.plugin.isMockBukkitServer()) {
            Logger.info("You are not using a database, data is kept in memory until the server stops.");
        }

        MessageBroker broker = candidate.broker().orElse(null);
        if (configuration.messagingType().equalsIgnoreCase("REDIS") && !key.equals(ZMenuKeys.key("redis"))) {
            try {
                this.redisMessaging = RedisMessaging.open(this.plugin.getConfig(), serverId, this.plugin.getLogger());
                broker = this.redisMessaging.broker();
            } catch (RuntimeException exception) {
                Logger.error("Unable to connect to Redis for messaging-type REDIS, changes are not sent to the other servers", exception);
            }
        }

        this.variableService = new ZVariableService(this.plugin, candidate.variables(), serverId, broker,
                candidate.sessions().orElse(null), configuration.handoffTimeoutMillis());
        if (candidate.capabilities().contains(StorageCapability.SHARED) && broker == null) {
            Logger.info("This database can be shared by several servers, but messaging-type is NONE: a server only sees the changes of the others when it reads them again.");
        }
        this.variableService.enable();
        this.plugin.getServer().getPluginManager().registerEvents(this.variableService, this.plugin);

        this.startBatchTask(configuration.batchSeconds());
    }

    /**
     * @return the variables of every scope, or null when the storage could not be opened
     */
    public @Nullable ZVariableService getVariableService() {
        return this.variableService;
    }

    public CompletableFuture<Optional<String>> loadSavedInventory(@NonNull UUID playerId) {
        if (!this.isEnable()) return CompletableFuture.completedFuture(Optional.empty());

        return this.provider.savedInventories().load(playerId);
    }

    private VariableRef playerRef(UUID playerId, String key) {
        return VariableRef.player(playerId, key);
    }

    /**
     * @return the provider in use, or null when the storage could not be opened
     */
    public @Nullable StorageProvider getProvider() {
        return this.provider;
    }

    private void startBatchTask(int seconds) {

        if (seconds <= 0) return;

        this.plugin.getScheduler().runTimerAsync(() -> this.storeBuffered(false), seconds, seconds, TimeUnit.SECONDS);
    }

    /**
     * Hands the buffered values and log entries to the provider.
     *
     * @param all whether to also write the player changes still waiting for their player to be
     *            read, when zMenu stops
     * @return completes once they are stored
     */
    private CompletableFuture<Void> storeBuffered(boolean all) {
        if (!this.isEnable()) return CompletableFuture.completedFuture(null);

        List<CompletableFuture<Void>> writes = new ArrayList<>();

        List<InventoryOpenRecord> records = this.cache.drain(InventoryOpenRecord.class);
        if (!records.isEmpty()) {
            this.provider.inventoryLogs().ifPresent(store -> writes.add(this.logFailure(store.append(records), "store the opened inventories")));
        }

        if (this.variableService != null) writes.add(this.variableService.flush(all));

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
            this.storeBuffered(true).get(FLUSH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
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

        if (this.variableService != null) this.variableService.disable();
        if (this.redisMessaging != null) {
            try {
                this.redisMessaging.close();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
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

        this.storeBuffered(false).whenComplete((result, throwable) -> this.flushing.set(false));
    }

    @Override
    public boolean isEnable() {
        return this.provider != null;
    }

    /**
     * Replaces a player data value, through the variables of the player scope.
     */
    @Override
    public void upsertData(@NonNull UUID uuid, @NonNull Data data) {
        if (!this.isEnable()) return;

        Object value = data.getValue();
        this.variableService.set(this.playerRef(uuid, data.getKey()), value == null ? "" : value.toString(), data.getExpiredAt());
    }

    @Override
    public void clearData() {
        if (!this.isEnable()) return;

        this.variableService.deleteScope(VariableScope.PLAYER);
    }

    @Override
    public void clearData(@NonNull UUID uniqueId) {
        if (!this.isEnable()) return;

        this.variableService.deleteOwner(VariableScope.PLAYER, uniqueId.toString());
    }

    @Override
    public void clearData(@NonNull String key) {
        if (!this.isEnable()) return;

        this.variableService.deleteName(VariableScope.PLAYER, key);
    }

    @Override
    public void removeData(@NonNull UUID uuid, @NonNull String key) {
        if (!this.isEnable()) return;

        this.variableService.delete(this.playerRef(uuid, key));
    }

    /**
     * The player data values in memory: online players and players looked up recently. Player
     * data is no longer read for every player.
     */
    @Override
    public @NonNull List<DataDTO> loadPlayers() {
        if (!this.isEnable()) return List.of();

        List<DataDTO> dataList = new ArrayList<>();
        for (Variable variable : this.variableService.getCachedScope(VariableScope.PLAYER)) {
            dataList.add(new DataDTO(UUID.fromString(variable.ref().owner()), variable.ref().name(), variable.text(), variable.expiresAt() == 0 ? null : new Date(variable.expiresAt())));
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
