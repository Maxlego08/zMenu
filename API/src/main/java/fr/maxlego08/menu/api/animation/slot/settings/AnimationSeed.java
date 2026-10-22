package fr.maxlego08.menu.api.animation.slot.settings;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Where the randomness of an animation comes from.
 *
 * <p>By default every play rolls afresh. Seeding it from the player instead turns variety into
 * personality: a given player always gets the same variant, the same scatter and the same choices,
 * so the menu looks like theirs rather than looking different every time they open it.</p>
 *
 * @param type  the kind of seed
 * @param value the seed itself, only used by {@link Type#FIXED}
 */
public record AnimationSeed(@NotNull Type type, long value) {

    public enum Type {

        /**
         * A fresh roll on every play.
         */
        RANDOM,

        /**
         * The same roll every time, for a given player and a given animation.
         */
        PLAYER,

        /**
         * The same roll for everyone, every time.
         */
        FIXED
    }

    /**
     * A fresh roll on every play, which is what an animation does unless it says otherwise.
     */
    public static final AnimationSeed RANDOM = new AnimationSeed(Type.RANDOM, 0);

    /**
     * Parses a seed from a configuration value.
     *
     * <p>{@code random} rolls afresh, {@code player} is stable per player, and a number is stable
     * for everyone.</p>
     *
     * @param value the value to parse, can be null
     * @return the seed, {@link #RANDOM} if the value is null or not understood
     */
    @Contract(pure = true)
    @NotNull
    public static AnimationSeed parse(@Nullable String value) {
        if (value == null) return RANDOM;

        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        if (trimmed.isEmpty() || trimmed.equals("random")) return RANDOM;
        if (trimmed.equals("player")) return new AnimationSeed(Type.PLAYER, 0);

        try {
            return new AnimationSeed(Type.FIXED, Long.parseLong(trimmed));
        } catch (NumberFormatException exception) {
            return RANDOM;
        }
    }

    /**
     * Gets the value this seed is written as, the way {@link #parse(String)} reads it back.
     *
     * @return {@code random}, {@code player}, or the fixed number
     */
    @Contract(pure = true)
    @NotNull
    public Object serialize() {
        return switch (this.type) {
            case RANDOM -> "random";
            case PLAYER -> "player";
            case FIXED -> this.value;
        };
    }

    /**
     * Builds the source the animation takes its rolls from.
     *
     * <p>The name of the animation is mixed in, so two animations seeded from the same player do not
     * end up producing the same pattern as one another.</p>
     *
     * @param playerId      the player the animation is played for, null outside of one
     * @param animationName the name of the animation
     * @return the source
     */
    @NotNull
    public Random create(@Nullable UUID playerId, @NotNull String animationName) {
        return switch (this.type) {
            case RANDOM -> ThreadLocalRandom.current();
            case FIXED -> new Random(this.value + animationName.hashCode());
            case PLAYER -> playerId == null
                    ? ThreadLocalRandom.current()
                    : new Random((playerId.getMostSignificantBits() ^ playerId.getLeastSignificantBits()) * 31 + animationName.hashCode());
        };
    }
}
