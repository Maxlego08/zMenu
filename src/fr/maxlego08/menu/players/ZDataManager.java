package fr.maxlego08.menu.players;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.builder.TimerBuilder;
import fr.maxlego08.menu.zcore.utils.interfaces.ReturnConsumer;
import fr.maxlego08.menu.zcore.utils.storage.Persist;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ZDataManager implements DataManager {

    private static Map<UUID, ZPlayerData> players = new HashMap<>();
    private final transient MenuPlugin plugin;
    private transient Map<String, String> defaultValues = new HashMap<>();
    private transient long lastSave;

    public ZDataManager(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
        this.defaultValues = new HashMap<>();
    }

    @Override
    public void save(Persist persist) {
        persist.save(this, "players");
    }

    @Override
    public void load(Persist persist) {
        persist.loadOrSaveDefault(this, ZDataManager.class, "players");
    }

    @Override
    public Optional<PlayerData> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(players.getOrDefault(uniqueId, null));
    }

    @Override
    public PlayerData getOrCreate(UUID uniqueId) {

        Optional<PlayerData> optional = this.getPlayer(uniqueId);
        if (optional.isPresent()) {
            return optional.get();
        }

        ZPlayerData data = new ZPlayerData(uniqueId);
        players.put(uniqueId, data);

        this.autoSave();

        return data;
    }

    @Override
    public void addData(UUID uniqueId, Data data) {
        PlayerData playerData = this.getOrCreate(uniqueId);
        playerData.addData(data);
    }

    @Override
    public Optional<Data> getData(UUID uniqueId, String key) {

        Optional<PlayerData> optional = this.getPlayer(uniqueId);
        if (!optional.isPresent()) {
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
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
            if (!optional.isPresent()) {
                return new ArrayList<>();
            }

            PlayerData playerData = optional.get();
            return playerData.getDatas().stream().map(Data::getKey).collect(Collectors.toList());

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void clearAll() {
        players.clear();
        this.save(this.plugin.getPersist());
    }

    @Override
    public void autoSave() {
        if (System.currentTimeMillis() > this.lastSave) {
            this.plugin.getScheduler().runTaskAsynchronously(() -> {
                this.save(this.plugin.getPersist());
                this.lastSave = System.currentTimeMillis() + (Config.secondsSavePlayerData * 1000L);
            });
        }
    }

    @Override
    public void clearPlayer(UUID uniqueId) {
        players.remove(uniqueId);
        this.autoSave();
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

    public void registerPlaceholder(LocalPlaceholder localPlaceholder) {

        localPlaceholder.register("player_key_exist_", (offlinePlayer, key) -> {

            Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
            if (!optional.isPresent()) return "false";

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
            if (!optional.isPresent()) return "true";

            PlayerData playerData = optional.get();
            Optional<Data> optionalData = playerData.getData(key);
            return optionalData.map(data -> String.valueOf(data.isExpired())).orElse("true");
        });
    }

    private String handlePlaceholder(OfflinePlayer offlinePlayer, String key, ReturnConsumer<Data, String> consumer) {
        Optional<PlayerData> optional = this.getPlayer(offlinePlayer.getUniqueId());
        if (!optional.isPresent()) return getDefaultKey(key);

        PlayerData playerData = optional.get();
        Optional<Data> optionalData = playerData.getData(key);
        return optionalData.isPresent() ? consumer.accept(optionalData.get()) : getDefaultKey(key);
    }

    private String getDefaultKey(String key) {
        return this.defaultValues.containsKey(key) ? this.defaultValues.get(key) : "Key '" + key + "' doesn't exist for this player";
    }

}
