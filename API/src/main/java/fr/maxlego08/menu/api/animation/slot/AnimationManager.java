package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Holds the named animations, so that an inventory, an action or another plugin can use one by key.
 *
 * <p>They live in the reloadable {@link Registries#ANIMATION} registry: the files of the
 * {@code animations} folder are known as {@code zmenu:<file name>}, and the animations registered by a
 * plugin are provided again on every reload, so a plugin registers its own once, at startup.</p>
 */
public interface AnimationManager {

    /**
     * Gets an animation by its key.
     *
     * @param key the key of the animation
     * @return the animation, if it exists
     */
    @NotNull
    Optional<Animation> getAnimation(@NotNull Key key);

    /**
     * Finds an animation from a name written in a file: {@code pulse} is the file
     * {@code animations/pulse.yml}, {@code myplugin:glow} an animation registered by a plugin.
     *
     * @param name the name of the animation
     * @return the animation, if it exists
     */
    @NotNull
    Optional<Animation> findAnimation(@NotNull String name);

    /**
     * Gets every animation currently known.
     *
     * @return the animations
     */
    @NotNull
    Collection<Animation> getAnimations();

    /**
     * Registers an animation, owned by a plugin. Give it a key in the namespace of the plugin, such as
     * {@code myplugin:glow}: the {@code zmenu} namespace holds the files of the animations folder.
     *
     * <p>The animation survives a reload of the folder, so a plugin only has to register it once.</p>
     *
     * @param plugin    the plugin registering it
     * @param animation the animation to register
     * @throws fr.maxlego08.menu.api.registry.RegistrationException if an animation already has this key
     */
    void registerAnimation(@NotNull Plugin plugin, @NotNull Animation animation);

    /**
     * Registers an animation written in code.
     *
     * @param plugin    the plugin registering it
     * @param animation the animation to register
     * @see CustomAnimation
     */
    default void registerAnimation(@NotNull Plugin plugin, @NotNull CustomAnimation animation) {
        this.registerAnimation(plugin, animation.toAnimation());
    }

    /**
     * Removes an animation registered by a plugin.
     *
     * @param key the key of the animation
     * @return whether an animation registered by a plugin had this key
     */
    boolean unregisterAnimation(@NotNull Key key);

    /**
     * Reloads the animations: the files of the {@code animations} folder are read again, and the
     * animations registered by plugins are kept. A file that cannot be read keeps its previous version.
     *
     * @return what the reload found
     */
    @NotNull
    ReloadReport loadAnimations();

    /**
     * Plays a named animation on the inventory a player is looking at.
     *
     * @param player   the player
     * @param key      the key of the animation
     * @param playback how to play it
     * @return how long the animation lasts, in milliseconds, 0 if the player is not looking at a
     *         zMenu inventory, the animation does not exist, or it had nothing to play
     */
    long play(@NotNull Player player, @NotNull Key key, @NotNull AnimationPlayback playback);

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

    /**
     * Gets which opening animations each player has already seen.
     *
     * @return the history of played animations
     */
    @NotNull
    AnimationHistory getHistory();
}
