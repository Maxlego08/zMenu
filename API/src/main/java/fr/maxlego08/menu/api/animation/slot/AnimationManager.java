package fr.maxlego08.menu.api.animation.slot;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Holds the animations of the {@code animations} folder, so that an inventory, an action or another
 * plugin can use one by name.
 *
 * <p>Animations registered by a plugin live alongside the ones loaded from disk, but are kept when
 * the folder is reloaded: a plugin registers its own once, at startup.</p>
 */
public interface AnimationManager {

    /**
     * Gets an animation by name, ignoring the case.
     *
     * @param name the name of the animation
     * @return the animation, if it exists
     */
    @NotNull
    Optional<Animation> getAnimation(@NotNull String name);

    /**
     * Gets every animation currently known.
     *
     * @return the animations
     */
    @NotNull
    Collection<Animation> getAnimations();

    /**
     * Registers an animation, replacing any animation of the same name.
     *
     * <p>An animation registered this way survives a reload of the folder, so a plugin only has to
     * register it once.</p>
     *
     * @param animation the animation to register
     */
    void registerAnimation(@NotNull Animation animation);

    /**
     * Registers an animation written in code.
     *
     * @param animation the animation to register
     * @see CustomAnimation
     */
    default void registerAnimation(@NotNull CustomAnimation animation) {
        this.registerAnimation(animation.toAnimation());
    }

    /**
     * Removes an animation.
     *
     * @param name the name of the animation
     */
    void unregisterAnimation(@NotNull String name);

    /**
     * Reloads every animation from the {@code animations} folder, keeping the ones registered by a
     * plugin.
     */
    void loadAnimations();

    /**
     * Plays an animation on the inventory a player is looking at.
     *
     * @param player    the player
     * @param name      the name of the animation
     * @param playback  how to play it
     * @return how long the animation lasts, in milliseconds, 0 if the player is not looking at a
     *         zMenu inventory, the animation does not exist, or it had nothing to play
     */
    long play(@NotNull Player player, @NotNull String name, @NotNull AnimationPlayback playback);

    /**
     * Plays an animation on the inventory a player is looking at.
     *
     * @param player    the player
     * @param animation the animation to play
     * @param playback  how to play it
     * @return how long the animation lasts, in milliseconds, 0 if the player is not looking at a
     *         zMenu inventory or the animation had nothing to play
     */
    long play(@NotNull Player player, @NotNull Animation animation, @NotNull AnimationPlayback playback);

    /**
     * Finishes the animation playing on the inventory a player is looking at, applying everything
     * that is left at once.
     *
     * @param player the player
     */
    void stop(@NotNull Player player);
}
