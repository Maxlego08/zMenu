package fr.maxlego08.menu.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.interfaces.ReturnConsumer;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.common.utils.builder.TimerBuilder;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ZDataManager implements DataManager {

    private final Map<UUID, ZPlayerData> players = new HashMap<>();
    private final ZMenuPlugin plugin;
    private final Map<String, String> defaultValues = new HashMap<>();
    private long lastSave;

    public ZDataManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    /*public void save(Persist persist) {
        persist.save(this, "players");
    }

    public void load(Persist persist) {
        persist.loadOrSaveDefault(this, ZDataManager.class, "players");
    }*/

    @Override
    public @NonNull Optional<PlayerData> getPlayer(@NonNull UUID uniqueId) {
        return Optional.ofNullable(players.getOrDefault(uniqueId, null));
    }

    @Override
    public @NonNull PlayerData getOrCreate(@NonNull UUID uniqueId) {

        Optional<PlayerData> optional = this.getPlayer(uniqueId);
        if (optional.isPresent()) {
            return optional.get();
        }

        ZPlayerData data = new ZPlayerData(this.plugin.getStorageManager(), uniqueId);
        players.put(uniqueId, data);

        return data;
    }

    @Override
    public void addData(@NonNull UUID uniqueId, @NonNull Data data) {
        PlayerData playerData = this.getOrCreate(uniqueId);
        playerData.addData(data);
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

    @Override
    public void clearAll() {
        players.clear();
        this.plugin.getStorageManager().clearData();
    }

    @Override
    public void clearPlayer(@NonNull UUID uniqueId) {
        players.remove(uniqueId);
        this.plugin.getStorageManager().clearData(uniqueId);
    }

    @Override
    public void loadDefaultValues() {
        File file = new File(plugin.getDataFolder(), "default_values.yml");
        if (!file.exists()) {
            this.plugin.saveResource("default_values.yml", false);
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection configurationSection = configuration.getConfigurationSection("values");
        if (configurationSection == null) return;

        this.defaultValues.clear();
        configurationSection.getKeys(false).forEach(key -> this.defaultValues.put(key, configuration.getString("values." + key)));
    }

    @Override
    public void loadPlayers() {
        var playerDatas = this.plugin.getStorageManager().loadPlayers();
        for (DataDTO dto : playerDatas) {
            ZPlayerData playerData = this.players.computeIfAbsent(dto.player_id(), uuid -> new ZPlayerData(this.plugin.getStorageManager(), uuid));
            playerData.setData(dto);
        }
    }

    @Override
    public @NonNull List<String> getKeys() {
        Set<String> strings = new HashSet<>(this.defaultValues.keySet());
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
        this.plugin.getStorageManager().clearData(key);
    }

    public void registerPlaceholder(LocalPlaceholder localPlaceholder) {

        localPlaceholder.register("player_key_exist_", (offlinePlayer, key) -> {

            Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
            if (optional.isEmpty()) return "false";

            PlayerData playerData = optional.get();
            return String.valueOf(playerData.containsKey(key));

        });

        localPlaceholder.register("player_value_", (offlinePlayer, key) -> handlePlaceholder(offlinePlayer, key, data -> data.getValue().toString()));

        localPlaceholder.register("player_expire_format_", (offlinePlayer, key) -> handlePlaceholder(offlinePlayer, key, data -> {
            if (data.getExpiredAt() <= 0) {
                return Message.PLACEHOLDER_NEVER.getMessage();
            }

            long seconds = Math.abs(System.currentTimeMillis() - data.getExpiredAt()) / 1000;
            return TimerBuilder.getStringTime(seconds);
        }));

        localPlaceholder.register("player_expire_", (offlinePlayer, key) -> handlePlaceholder(offlinePlayer, key, data -> String.valueOf(data.getExpiredAt())));
        localPlaceholder.register("player_is_expired_", (offlinePlayer, key) -> {

            Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
            if (optional.isEmpty()) return "true";

            PlayerData playerData = optional.get();
            Optional<Data> optionalData = playerData.getData(key);
            return optionalData.map(data -> String.valueOf(data.isExpired())).orElse("true");
        });
    }

    private String handlePlaceholder(OfflinePlayer offlinePlayer, String key, ReturnConsumer<Data, String> consumer) {
        Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
        if (optional.isEmpty()) return getDefaultKey(key);

        PlayerData playerData = optional.get();
        Optional<Data> optionalData = playerData.getData(key);
        return optionalData.isPresent() ? consumer.accept(optionalData.get()) : getDefaultKey(key);
    }

    private String getDefaultKey(String key) {
        return this.defaultValues.containsKey(key) ? this.defaultValues.get(key) : "Key '" + key + "' doesn't exist for this player";
    }

    @Override
    public void convertOldDatas(@NonNull CommandSender sender) {
        var manager = plugin.getStorageManager();
        try {
            var datas = PlayerDataLoader.loadPlayerData(this.plugin.getDataFolder() + "/players.json");
            datas.forEach((uuid, playerData) -> playerData.forEach(data -> manager.upsertData(uuid, data)));
            this.plugin.getLogger().info("Loaded " + datas.size() + " players.");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
