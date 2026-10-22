package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * An animation known by a key: a file of the {@code animations} folder ({@code zmenu:<file name>}),
 * one registered by a plugin, or the animation of an inventory (the key of the inventory).
 *
 * <p>The {@code animation} action plays a named one with {@code file: pulse}, or
 * {@code file: myplugin:glow} for one registered by a plugin. The {@code open-animation} and
 * {@code close-animation} of an inventory only read files of the animations folder.</p>
 *
 * @param key      the key the animation is known by
 * @param settings what the animation does
 */
public record Animation(@NotNull Key key, @NotNull SlotAnimationSettings settings) implements Keyed {

    /**
     * The key as text, {@code namespace:value}, used in messages and to derive the random seed of
     * the animation for a player.
     *
     * @return the name of the animation
     */
    @NotNull
    public String name() {
        return this.key.asString();
    }
}
