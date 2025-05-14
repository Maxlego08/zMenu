package fr.maxlego08.menu.api.players;

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
    Optional<PlayerData> getPlayer(UUID uniqueId);

    /**
     * @param uniqueId Player {@link UUID}
     * @return PlayerData
     */
    PlayerData getOrCreate(UUID uniqueId);

    /**
     * @param uniqueId Player {@link UUID}
     * @param data     New data
     */
    void addData(UUID uniqueId, Data data);

    /**
     * @param uniqueId Player {@link UUID}
     * @param key      Data key
     * @return Optional
     */
    Optional<Data> getData(UUID uniqueId, String key);

    /**
     * Clear all player's data
     */
    void clearAll();

    /**
     * Clear player's data
     *
     * @param uniqueId Player {@link UUID}
     */
    void clearPlayer(UUID uniqueId);

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
    List<String> getKeys();

    void clearKey(String key);
}
