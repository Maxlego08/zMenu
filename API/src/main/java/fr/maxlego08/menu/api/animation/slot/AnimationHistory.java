package fr.maxlego08.menu.api.animation.slot;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Remembers which opening animations a player has already seen, so that an animation played
 * {@link AnimationPlayMode#ONCE once} or with a cooldown is not played again too early.
 *
 * <p>Two memories are kept: one for the session, forgotten when the player leaves, and one stored in
 * the database for the animations that remember themselves beyond the session.</p>
 *
 * @see AnimationManager#getHistory()
 */
public interface AnimationHistory {

    /**
     * Reads back which animations a player has already seen, when they join.
     *
     * <p>Called once per connection. The rows are fetched off the main thread and kept in memory,
     * so that opening a menu never waits on the database.</p>
     *
     * @param player The player.
     */
    void load(@NotNull Player player);

    /**
     * Whether what a player has already seen is known yet.
     *
     * <p>False for the short moment between joining and the rows coming back, during which an
     * animation that only plays once is held back rather than risking spending its single play.</p>
     *
     * @param player The player.
     * @return true once the player's history is in memory.
     */
    boolean isLoaded(@NotNull Player player);

    /**
     * Forgets everything kept in memory for a player, when they disconnect. The database is not touched.
     *
     * @param uniqueId The unique id of the player.
     */
    void unload(@NotNull UUID uniqueId);

    /**
     * Returns whether an animation has already been played for a player since they connected.
     *
     * @param player       The player.
     * @param animationKey The key identifying the animation.
     * @return true if the animation has already been played.
     */
    boolean hasPlayedThisSession(@NotNull Player player, @NotNull String animationKey);

    /**
     * Remembers, until the player disconnects, that an animation has been played for them.
     *
     * @param player       The player.
     * @param animationKey The key identifying the animation.
     */
    void markPlayedThisSession(@NotNull Player player, @NotNull String animationKey);

    /**
     * When a player last saw an animation that remembers itself beyond the session.
     *
     * <p>Answered from memory: the rows are read once when the player joins, because a menu opens
     * far too often to ask the database each time.</p>
     *
     * @param player       The player.
     * @param animationKey The key identifying the animation.
     * @return the time it last played in milliseconds since the epoch, or null if it never has.
     */
    @Nullable
    Long getPlayedAt(@NotNull Player player, @NotNull String animationKey);

    /**
     * Remembers that an animation has played, in the database as well as in memory.
     *
     * @param player       The player.
     * @param animationKey The key identifying the animation.
     * @param playedAt     When it played, in milliseconds since the epoch.
     */
    void markPlayed(@NotNull Player player, @NotNull String animationKey, long playedAt);

    /**
     * Forgets that a player has seen an animation, in the session and in the database, so one that
     * only plays once plays again.
     *
     * @param uniqueId     The unique id of the player.
     * @param animationKey The animation to forget, or null to forget all of them.
     */
    void forget(@NotNull UUID uniqueId, @Nullable String animationKey);
}
