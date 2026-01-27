package fr.maxlego08.menu.api.players;

import org.jetbrains.annotations.NotNull;

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
    @NotNull
    UUID getUniqueId();

    /**
     * Allows to return list of player {@link Data}. Attention you cannot modify the values directly from this collection.
     *
     * @return collections
     */
    @NotNull
    Collection<Data> getDatas();

    /**
     * Allows you to add a data
     *
     * @param data New {@link Data}
     */
    void addData(@NotNull Data data);

    /**
     * Allows you to delete a data
     *
     * @param data Old {@link Data}
     */
    void removeData(@NotNull Data data);

    /**
     * Allows you to delete a {@link Data}
     *
     * @param key Data key
     */
    void removeData(@NotNull String key);

    /**
     * Check if data exist
     *
     * @param key Data key
     * @return boolean
     */
    boolean containsKey(@NotNull String key);

    /**
     * Get data
     *
     * @param key Data key
     * @return optional
     */
    @NotNull
    Optional<Data> getData(@NotNull String key);

}
