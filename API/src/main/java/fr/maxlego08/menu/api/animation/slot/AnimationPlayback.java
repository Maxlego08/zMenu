package fr.maxlego08.menu.api.animation.slot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * How an animation is played on an inventory.
 */
public enum AnimationPlayback {

    /**
     * The buttons are hidden and revealed again. This is what opening an inventory does.
     */
    REVEAL,

    /**
     * Only the temporary items play. The buttons are never hidden and keep working, so the animation
     * draws over a menu the player is already looking at.
     *
     * <p>Group boundaries still mean what they would have meant: {@code group:2} is the moment group
     * 2 would have started, so an animation reads the same whichever way it is played.</p>
     */
    OVERLAY,

    /**
     * The buttons are taken away one by one, in the order the animation gives. This is the reverse
     * of {@link #REVEAL}, and is what a menu closing itself does.
     *
     * <p>Nothing is hidden up front: the menu is whole when it starts and empty when it ends.</p>
     */
    CONCEAL;

    /**
     * Whether this playback hides the buttons before putting them back.
     *
     * @return true for {@link #REVEAL} only
     */
    public boolean hidesButtons() {
        return this == REVEAL;
    }

    /**
     * Parses a playback from a configuration value.
     *
     * @param value        the value to parse, can be null
     * @param defaultValue the value to return if the given value is null or invalid
     * @return the parsed playback
     */
    @Contract(pure = true)
    @NotNull
    public static AnimationPlayback parse(@Nullable String value, @NotNull AnimationPlayback defaultValue) {
        if (value == null) return defaultValue;
        try {
            return AnimationPlayback.valueOf(value.toUpperCase(Locale.ROOT).replace('-', '_').trim());
        } catch (IllegalArgumentException exception) {
            return defaultValue;
        }
    }
}
