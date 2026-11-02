package fr.maxlego08.menu.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.common.utils.builder.TimerBuilder;
import fr.maxlego08.menu.variables.ZVariableService;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Player data, the {@link VariableScope#PLAYER} variables of the {@link ZVariableService}.
 *
 * <p>A player's values are read before they join and kept while they are online; an offline
 * player is read when looked up and kept for a few minutes. See {@link ZVariableService}.</p>
 */
public class ZDataManager implements DataManager {

    private final ZMenuPlugin plugin;
    private final Map<String, String> defaultValues = new HashMap<>();

    public ZDataManager(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    private ZVariableService variables() {
        return this.plugin.getVariableService();
    }

    @Override
    public @NonNull Optional<PlayerData> getPlayer(@NonNull UUID uniqueId) {
        if (!this.variables().isLoaded(VariableScope.PLAYER, uniqueId.toString())) return Optional.empty();
        return Optional.of(new ZPlayerData(this.variables(), uniqueId));
    }

    @Override
    public @NonNull PlayerData getOrCreate(@NonNull UUID uniqueId) {
        return new ZPlayerData(this.variables(), uniqueId);
    }

    @Override
    public void addData(@NonNull UUID uniqueId, @NonNull Data data) {
        this.getOrCreate(uniqueId).addData(data);
    }

    /**
     * Reads the values of a player from the database, unless they are already in memory.
     */
    @Override
    public @NonNull CompletableFuture<PlayerData> loadPlayer(@NonNull UUID uniqueId) {
        return this.variables().load(VariableScope.PLAYER, uniqueId.toString()).thenApply(variables -> new ZPlayerData(this.variables(), uniqueId));
    }

    @Override
    public @NonNull Optional<Data> getData(@NonNull UUID uniqueId, @NonNull String key) {
        return this.variables().getCached(VariableRef.player(uniqueId, key)).map(ZPlayerData::toData);
    }

    public List<String> getKeys(String[] args) {
        if (args.length != 4) {
            return new ArrayList<>();
        }
        try {
            OfflinePlayer offlinePlayer = OfflinePlayerCache.get(args[2]);
            return this.getKeys(offlinePlayer.getUniqueId());
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
        String owner = playerId.toString();
        if (!this.variables().isLoaded(VariableScope.PLAYER, owner)) {
            this.loadPlayer(playerId);
            return new ArrayList<>();
        }

        List<String> keys = new ArrayList<>();
        for (Variable variable : this.variables().getCachedAll(VariableScope.PLAYER, owner)) {
            keys.add(variable.ref().name());
        }
        return keys;
    }

    @Override
    public void clearAll() {
        this.variables().deleteScope(VariableScope.PLAYER);
    }

    @Override
    public void clearPlayer(@NonNull UUID uniqueId) {
        this.variables().deleteOwner(VariableScope.PLAYER, uniqueId.toString());
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
     * Nothing to do: the variable service reads the players already online when it starts, and
     * the others when they join or are looked up.
     */
    @Override
    public void loadPlayers() {
    }

    @Override
    public @NonNull List<String> getKeys() {
        Set<String> strings = new HashSet<>(this.defaultValues.keySet());
        strings.addAll(this.variables().names(VariableScope.PLAYER));
        return new ArrayList<>(strings);
    }

    @Override
    public void clearKey(@NonNull String key) {
        this.variables().deleteName(VariableScope.PLAYER, key);
    }

    /**
     * The variable of a placeholder, or empty when its owner is not in memory yet. An owner who is
     * not is read in the background, so the placeholder shows the real value on its next update.
     */
    private Optional<Variable> placeholderVariable(VariableScope scope, OfflinePlayer offlinePlayer, String key) {
        ZVariableService variables = this.variables();
        String owner = variables.ownerOf(scope, offlinePlayer == null ? null : offlinePlayer.getUniqueId());
        if (!variables.isLoaded(scope, owner)) {
            variables.load(scope, owner);
            return Optional.empty();
        }
        return variables.getCached(new VariableRef(scope, owner, key));
    }

    /**
     * Registers {@code <scope>_key_exist_}, {@code <scope>_value_}, {@code <scope>_expire_format_},
     * {@code <scope>_expire_} and {@code <scope>_is_expired_} for the player, server and global
     * scopes, such as {@code %zmenu_global_value_event_started%}.
     */
    public void registerPlaceholder(LocalPlaceholder localPlaceholder) {
        for (VariableScope scope : VariableScope.values()) {
            String prefix = scope.id() + "_";

            localPlaceholder.register(prefix + "key_exist_", (offlinePlayer, key) -> {
                if (scope == VariableScope.PLAYER && offlinePlayer == null) return "false";
                return String.valueOf(this.placeholderVariable(scope, offlinePlayer, key).isPresent());
            });

            localPlaceholder.register(prefix + "value_", (offlinePlayer, key) -> this.handlePlaceholder(scope, offlinePlayer, key, Variable::text));

            localPlaceholder.register(prefix + "expire_format_", (offlinePlayer, key) -> this.handlePlaceholder(scope, offlinePlayer, key, variable -> {
                if (variable.expiresAt() <= 0) {
                    return Message.PLACEHOLDER_NEVER.getMessage();
                }

                long seconds = Math.abs(System.currentTimeMillis() - variable.expiresAt()) / 1000;
                return TimerBuilder.getStringTime(seconds);
            }));

            localPlaceholder.register(prefix + "expire_", (offlinePlayer, key) -> this.handlePlaceholder(scope, offlinePlayer, key, variable -> String.valueOf(variable.expiresAt())));

            localPlaceholder.register(prefix + "is_expired_", (offlinePlayer, key) -> {
                if (scope == VariableScope.PLAYER && offlinePlayer == null) return "true";
                return String.valueOf(this.placeholderVariable(scope, offlinePlayer, key).isEmpty());
            });
        }
    }

    private String handlePlaceholder(VariableScope scope, OfflinePlayer offlinePlayer, String key, Function<Variable, String> consumer) {
        if (scope == VariableScope.PLAYER && offlinePlayer == null) return this.getDefaultKey(key);
        return this.placeholderVariable(scope, offlinePlayer, key).map(consumer).orElseGet(() -> this.getDefaultKey(key));
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
