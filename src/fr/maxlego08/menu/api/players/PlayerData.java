package fr.maxlego08.menu.api.players;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>Player's {@link Data}</p>
 */
public interface PlayerData {

    /**
     * Return uuid of player
     *
     * @return uniqueId
     */
    UUID getUniqueId();

    /**
     * Allows to return list of player {@link Data}. Attention you cannot modify the values directly from this collection.
     *
     * @return collections
     */
    Collection<Data> getDatas();

    /**
     * Allows you to add a data
     *
     * @param data New {@link Data}
     */
    void addData(Data data);

    /**
     * Allows you to delete a data
     *
     * @param data Old {@link Data}
     */
    void removeData(Data data);

    /**
     * Allows you to delete a {@link Data}
     *
     * @param key Data key
     */
    void removeData(String key);

    /**
     * Check if data exist
     *
     * @param key Data key
     * @return boolean
     */
    boolean containsKey(String key);

    /**
     * Get data
     *
     * @param key Data key
     * @return optional
     */
    Optional<Data> getData(String key);

}
