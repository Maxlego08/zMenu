package fr.maxlego08.menu.api.animation.slot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * When the opening animation of an inventory is allowed to play.
 *
 * <p>Refreshing an inventory never plays the animation, whatever the mode: a page change or a
 * {@code refresh inventory} action re-opens the same menu, and replaying the whole animation there
 * would make a paginated menu unusable.</p>
 */
public enum AnimationPlayMode {

    /**
     * Plays every time the player opens the inventory.
     */
    ALWAYS,

    /**
     * Plays the first time the player opens this inventory, then never again until they reconnect.
     */
    ONCE,

    /**
     * Plays the first time the player opens this inventory and never again, remembered in the
     * database so it survives a reconnect and a restart.
     *
     * <p>Falls back to {@link #ONCE} when the database is disabled, since there is then nowhere to
     * remember it.</p>
     */
    ONCE_FOREVER;

    /**
     * Whether this mode remembers itself beyond the session.
     *
     * @return true for {@link #ONCE_FOREVER}
     */
    public boolean isPersistent() {
        return this == ONCE_FOREVER;
    }

    /**
     * Whether this mode plays at most once.
     *
     * @return true for {@link #ONCE} and {@link #ONCE_FOREVER}
     */
    public boolean isOnce() {
        return this == ONCE || this == ONCE_FOREVER;
    }

    /**
     * Decides whether an animation may play, from when the player last saw it.
     *
     * <p>A cooldown and a once mode are independent: the cooldown alone turns {@link #ALWAYS} into
     * "at most this often", and a once mode ignores it because it never gets a second chance
     * anyway.</p>
     *
     * @param lastPlayedAt when the player last saw it, in milliseconds since the epoch, or null if
     *                     they never have
     * @param now          the current time, in milliseconds since the epoch
     * @param cooldown     the minimum time between two plays, in milliseconds; 0 for none
     * @return true if the animation may play
     */
    @Contract(pure = true)
    public boolean canPlay(@Nullable Long lastPlayedAt, long now, long cooldown) {
        if (lastPlayedAt == null) return true;
        if (this.isOnce()) return false;
        return cooldown <= 0 || now - lastPlayedAt >= cooldown;
    }

    /**
     * Parses a mode from a configuration value.
     *
     * @param value        the value to parse, can be null
     * @param defaultValue the value to return if the given value is null or invalid
     * @return the parsed mode
     */
    @Contract(pure = true)
    @NotNull
    public static AnimationPlayMode parse(@Nullable String value, @NotNull AnimationPlayMode defaultValue) {
        if (value == null) return defaultValue;
        String name = value.toUpperCase(Locale.ROOT).replace('-', '_').trim();
        if (name.equals("FIRST_OPEN")) return ONCE;
        try {
            return AnimationPlayMode.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return defaultValue;
        }
    }
}
