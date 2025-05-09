package fr.maxlego08.menu.api.players;

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
     * Save auto
     */
    void autoSave();

    /**
     * Clear player's data
     *
     * @param uniqueId Player {@link UUID}
     */
    void clearPlayer(UUID uniqueId);

    void loadDefaultValues();

}
