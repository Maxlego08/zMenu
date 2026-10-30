package fr.maxlego08.menu.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.common.utils.builder.TimerBuilder;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Player data, kept in memory only for the players who need it.
 *
 * <p>A player's values are read from the database before they join and kept while they are
 * online. A player who is offline, looked up by a command or a placeholder, is read in the
 * background and kept for {@link #OFFLINE_TTL_MILLIS} after the last lookup. Every value used to be
 * read at startup and kept for the whole uptime, for every player who ever joined.</p>
 */
public class ZDataManager implements DataManager, Listener {

    private static final long OFFLINE_TTL_MILLIS = TimeUnit.MINUTES.toMillis(5);
    private static final long PRE_LOGIN_TIMEOUT_SECONDS = 10;

    private final Map<UUID, ZPlayerData> players = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<PlayerData>> loading = new ConcurrentHashMap<>();
    private final Set<String> knownKeys = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean evictionStarted = new AtomicBoolean(false);
    private final ZMenuPlugin plugin;
    private final Map<String, String> defaultValues = new HashMap<>();

    public ZDataManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public @NonNull Optional<PlayerData> getPlayer(@NonNull UUID uniqueId) {
        ZPlayerData playerData = this.players.get(uniqueId);
        if (playerData != null) playerData.touch();
        return Optional.ofNullable(playerData);
    }

    @Override
    public @NonNull PlayerData getOrCreate(@NonNull UUID uniqueId) {
        ZPlayerData playerData = this.players.computeIfAbsent(uniqueId, id -> new ZPlayerData(this.plugin.getStorageManager(), id));
        playerData.touch();
        return playerData;
    }

    @Override
    public void addData(@NonNull UUID uniqueId, @NonNull Data data) {
        PlayerData playerData = this.getOrCreate(uniqueId);
        playerData.addData(data);
        this.knownKeys.add(data.getKey());
    }

    /**
     * Reads the values of a player from the database, unless they are already in memory. Two
     * calls for the same player share one read.
     */
    @Override
    public @NonNull CompletableFuture<PlayerData> loadPlayer(@NonNull UUID uniqueId) {
        ZPlayerData existing = this.players.get(uniqueId);
        if (existing != null && existing.isLoaded()) {
            existing.touch();
            return CompletableFuture.completedFuture(existing);
        }

        CompletableFuture<PlayerData> created = new CompletableFuture<>();
        CompletableFuture<PlayerData> running = this.loading.putIfAbsent(uniqueId, created);
        if (running != null) return running;

        ((ZStorageManager) this.plugin.getStorageManager()).loadPlayerData(uniqueId).whenComplete((entries, throwable) -> {
            this.loading.remove(uniqueId, created);
            if (throwable != null) {
                created.completeExceptionally(throwable);
                return;
            }
            ZPlayerData playerData = this.players.computeIfAbsent(uniqueId, id -> new ZPlayerData(this.plugin.getStorageManager(), id));
            playerData.merge(entries);
            playerData.touch();
            entries.forEach(entry -> this.knownKeys.add(entry.key()));
            created.complete(playerData);
        });
        return created;
    }

    /**
     * Reads the values of a player before they join, so a menu never sees a player whose values
     * are missing and overwrites them. If they cannot be read the player is turned away, since
     * adding to a value that was not read would replace it.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        try {
            this.loadPlayer(event.getUniqueId()).get(PRE_LOGIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            this.refuseLogin(event, exception);
        } catch (Exception exception) {
            this.refuseLogin(event, exception);
        }
    }

    private void refuseLogin(AsyncPlayerPreLoginEvent event, Exception exception) {
        Logger.error("Unable to load the player data of " + event.getName() + " (" + event.getUniqueId() + ")", exception);
        String message = Message.PLAYERS_DATA_LOAD_ERROR.getMessage();
        Component reason = this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater ? paperMetaUpdater.getComponent(message) : Component.text(message);
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, reason);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        this.loadPlayer(uniqueId).thenAccept(playerData -> ((ZPlayerData) playerData).setOnline(true));
    }

    /**
     * Keeps the values of a player who leaves for {@link #OFFLINE_TTL_MILLIS}, so a player who
     * reconnects at once never reads values older than the ones still waiting to be written.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        ZPlayerData playerData = this.players.get(event.getPlayer().getUniqueId());
        if (playerData != null) playerData.setOnline(false);
    }

    /**
     * Forgets offline players nobody looked up recently, once nothing of theirs waits to be
     * written.
     */
    private void evictOfflinePlayers() {
        long limit = System.currentTimeMillis() - OFFLINE_TTL_MILLIS;
        ZStorageManager storageManager = (ZStorageManager) this.plugin.getStorageManager();
        this.players.entrySet().removeIf(entry -> {
            ZPlayerData playerData = entry.getValue();
            return !playerData.isOnline() && playerData.getLastAccess() < limit && !storageManager.hasPendingData(entry.getKey());
        });
    }

    /**
     * @return how many players have their values in memory
     */
    public int getCachedPlayers() {
        return this.players.size();
    }

    @Override
    public @NonNull Optional<Data> getData(@NonNull UUID uniqueId, @NonNull String key) {

        Optional<PlayerData> optional = this.getPlayer(uniqueId);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        PlayerData playerData = optional.get();
        return playerData.getData(key);
    }

    public List<String> getKeys(String[] args) {
        if (args.length != 4) {
            return new ArrayList<>();
        }
        try {
            String playerName = args[2];
            OfflinePlayer offlinePlayer = OfflinePlayerCache.get(playerName);
            Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
            if (optional.isEmpty()) {
                return new ArrayList<>();
            }

            PlayerData playerData = optional.get();
            List<String> keys = new ArrayList<>(playerData.getDatas().size());
            for (Data data : playerData.getDatas()) {
                keys.add(data.getKey());
            }
            return keys;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * The keys of a player in memory. An offline player who is not is read in the background, so
     * the keys show up on the next suggestion.
     */
    @Override
    public List<String> getKeys(@NotNull UUID playerId) {
        Optional<PlayerData> optional = this.getPlayer(playerId);
        if (optional.isEmpty()) {
            this.loadPlayer(playerId);
            return new ArrayList<>();
        }

        PlayerData playerData = optional.get();
        List<String> keys = new ArrayList<>(playerData.getDatas().size());
        for (Data data : playerData.getDatas()) {
            keys.add(data.getKey());
        }
        return keys;
    }

    @Override
    public void clearAll() {
        this.players.values().forEach(ZPlayerData::clearLocal);
        this.knownKeys.clear();
        this.plugin.getStorageManager().clearData();
    }

    @Override
    public void clearPlayer(@NonNull UUID uniqueId) {
        ZPlayerData playerData = this.players.get(uniqueId);
        if (playerData != null) playerData.clearLocal();
        this.plugin.getStorageManager().clearData(uniqueId);
    }

    @Override
    public void loadDefaultValues() {
        File file = new File(this.plugin.getDataFolder(), "default_values.yml");
        if (!file.exists()) {
            this.plugin.saveResource("default_values.yml", false);
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configurationSection = configuration.getConfigurationSection("values");
        if (configurationSection == null) return;

        this.defaultValues.clear();
        configurationSection.getKeys(false).forEach(key -> this.defaultValues.put(key, configuration.getString("values." + key)));
    }

    /**
     * Reads the values of the players already online, after a reload, and the list of keys used
     * for suggestions. Other players are read when they join or are looked up.
     */
    @Override
    public void loadPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.loadPlayer(player.getUniqueId()).thenAccept(playerData -> ((ZPlayerData) playerData).setOnline(true));
        }

        ((ZStorageManager) this.plugin.getStorageManager()).loadDataKeys().whenComplete((keys, throwable) -> {
            if (throwable != null) Logger.error("Unable to load the player data keys", throwable);
            else this.knownKeys.addAll(keys);
        });

        if (this.evictionStarted.compareAndSet(false, true)) {
            this.plugin.getScheduler().runTimerAsync(this::evictOfflinePlayers, 1, 1, TimeUnit.MINUTES);
        }
    }

    @Override
    public @NonNull List<String> getKeys() {
        Set<String> strings = new HashSet<>(this.defaultValues.keySet());
        strings.addAll(this.knownKeys);
        for (PlayerData playerData : this.players.values()) {
            for (Data data : playerData.getDatas()) {
                strings.add(data.getKey());
            }
        }
        return new ArrayList<>(strings);
    }

    @Override
    public void clearKey(@NonNull String key) {
        this.players.values().forEach(e -> e.removeData(key));
        this.knownKeys.remove(key);
        this.plugin.getStorageManager().clearData(key);
    }

    /**
     * The player data of a placeholder, or empty when the player is not in memory yet. A player who
     * is not is read in the background, so the placeholder shows the real value on its next update.
     */
    private Optional<PlayerData> placeholderPlayer(OfflinePlayer offlinePlayer) {
        Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
        if (optional.isEmpty()) this.loadPlayer(offlinePlayer.getUniqueId());
        return optional;
    }

    public void registerPlaceholder(LocalPlaceholder localPlaceholder) {

        localPlaceholder.register("player_key_exist_", (offlinePlayer, key) -> {

            Optional<PlayerData> optional = this.placeholderPlayer(offlinePlayer);
            if (optional.isEmpty()) return "false";

            PlayerData playerData = optional.get();
            return String.valueOf(playerData.containsKey(key));

        });

        localPlaceholder.register("player_value_", (offlinePlayer, key) -> this.handlePlaceholder(offlinePlayer, key, data -> data.getValue().toString()));

        localPlaceholder.register("player_expire_format_", (offlinePlayer, key) -> this.handlePlaceholder(offlinePlayer, key, data -> {
            if (data.getExpiredAt() <= 0) {
                return Message.PLACEHOLDER_NEVER.getMessage();
            }

            long seconds = Math.abs(System.currentTimeMillis() - data.getExpiredAt()) / 1000;
            return TimerBuilder.getStringTime(seconds);
        }));

        localPlaceholder.register("player_expire_", (offlinePlayer, key) -> this.handlePlaceholder(offlinePlayer, key, data -> String.valueOf(data.getExpiredAt())));
        localPlaceholder.register("player_is_expired_", (offlinePlayer, key) -> {

            Optional<PlayerData> optional = this.placeholderPlayer(offlinePlayer);
            if (optional.isEmpty()) return "true";

            PlayerData playerData = optional.get();
            Optional<Data> optionalData = playerData.getData(key);
            return optionalData.map(data -> String.valueOf(data.isExpired())).orElse("true");
        });
    }

    private String handlePlaceholder(OfflinePlayer offlinePlayer, String key, Function<Data, String> consumer) {
        Optional<PlayerData> optional = this.placeholderPlayer(offlinePlayer);
        if (optional.isEmpty()) return this.getDefaultKey(key);

        PlayerData playerData = optional.get();
        Optional<Data> optionalData = playerData.getData(key);
        return optionalData.isPresent() ? consumer.apply(optionalData.get()) : this.getDefaultKey(key);
    }

    private String getDefaultKey(String key) {
        return this.defaultValues.containsKey(key) ? this.defaultValues.get(key) : "Key '" + key + "' doesn't exist for this player";
    }

    @Override
    public void convertOldDatas(@NonNull CommandSender sender) {
        var manager = this.plugin.getStorageManager();
        try {
            var datas = PlayerDataLoader.loadPlayerData(this.plugin.getDataFolder() + "/players.json");
            datas.forEach((uuid, playerData) -> playerData.forEach(data -> manager.upsertData(uuid, data)));
            Logger.info("Loaded " + datas.size() + " players.", Logger.LogType.SUCCESS);
        } catch (IOException exception) {
            Logger.error(exception);
        }
    }
}
