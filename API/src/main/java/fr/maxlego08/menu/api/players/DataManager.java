package fr.maxlego08.menu.api.players;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>Player Data Management</p>
 */
public interface DataManager {

    /**
     * @param uniqueId Player {@link UUID}
     * @return optional
     */
    @NotNull
    Optional<PlayerData> getPlayer(@NotNull UUID uniqueId);

    /**
     * @param uniqueId Player {@link UUID}
     * @return PlayerData
     */
    @NotNull
    PlayerData getOrCreate(@NotNull UUID uniqueId);

    /**
     * @param uniqueId Player {@link UUID}
     * @param data     New data
     */
    void addData(@NotNull UUID uniqueId,@NotNull Data data);

    /**
     * @param uniqueId Player {@link UUID}
     * @param key      Data key
     * @return Optional
     */
    @NotNull
    Optional<Data> getData(@NotNull UUID uniqueId,@NotNull String key);

    /**
     * Clear all player's data
     */
    void clearAll();

    /**
     * Clear player's data
     *
     * @param uniqueId Player {@link UUID}
     */
    void clearPlayer(@NotNull UUID uniqueId);

    /**
     * Loads default values for each player.
     * This method is called when the plugin is started,
     * and should be used to load default values for each player.
     */
    void loadDefaultValues();

    /**
     * Loads the player data from the database or storage.
     * This method is responsible for retrieving and initializing
     * player data when the plugin is started or when player data is needed.
     */
    void loadPlayers();

    /**
     * Returns a list of all keys that are used in the player data.
     * These keys are used to store and retrieve data from the player data.
     *
     * @return a list of all keys that are used in the player data.
     */
    @NotNull
    List<String> getKeys();

    void clearKey(@NotNull String key);

    void convertOldDatas(@NotNull CommandSender sender);
}
