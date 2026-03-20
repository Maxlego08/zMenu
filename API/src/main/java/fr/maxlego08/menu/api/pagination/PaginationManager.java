package fr.maxlego08.menu.api.pagination;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PaginationManager {

    /**
     * Gets or creates a pagination state for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier (e.g., "job:miner", "reward:quest_1")
     * @return the pagination state
     */
    @NotNull PaginationState getOrCreateState(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Gets the current page for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     * @return the current page (0-based index), or 0 if not found
     */
    int getPage(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Sets the current page for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     * @param page      the page to set (0-based index)
     */
    void setPage(@NotNull UUID playerId, @NotNull String contextId, int page);

    /**
     * Increments the page for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     */
    void nextPage(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Decrements the page for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     */
    void previousPage(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Resets the pagination to page 0 for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     */
    void reset(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Gets the pagination state without creating it if it doesn't exist.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     * @return the pagination state, or null if not found
     */
    @Nullable PaginationState getState(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Returns whether a pagination state exists for a player and context.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     * @return true if a state exists
     */
    default boolean hasState(@NotNull UUID playerId, @NotNull String contextId) {
        return getState(playerId, contextId) != null;
    }

    /**
     * Removes a pagination state for a player and context.
     * Useful when the player logs out or the context is no longer needed.
     *
     * @param playerId  the player's UUID
     * @param contextId the context identifier
     */
    void removeState(@NotNull UUID playerId, @NotNull String contextId);

    /**
     * Removes all pagination states for a player.
     * Useful on player logout.
     *
     * @param playerId the player's UUID
     */
    void removePlayerStates(@NotNull UUID playerId);

    /**
     * Clears all pagination states.
     */
    void clearAll();
}