package fr.maxlego08.menu.hooks.dialogs.canvas.layout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * How a layout chooses the size of a child on one axis, written as a number, {@code fit}, {@code grow} or a
 * percentage such as {@code 50%}.
 *
 * @param kind  The kind of size.
 * @param value The pixels of a fixed size, or the percentage.
 */
public record SizeSpec(@NotNull Kind kind, int value) {

    public static final SizeSpec FIT = new SizeSpec(Kind.FIT, 0);
    public static final SizeSpec GROW = new SizeSpec(Kind.GROW, 0);

    public enum Kind {
        /**
         * A size in pixels.
         */
        FIXED,
        /**
         * The size the child would like.
         */
        FIT,
        /**
         * The size the child would like, plus a share of the space left in the container.
         */
        GROW,
        /**
         * A percentage of the inside of the container.
         */
        PERCENT
    }

    /**
     * @param value A number, or a text such as {@code fit}, {@code grow} or {@code 50%}.
     * @return The size, or null if the value is not one.
     */
    public static @Nullable SizeSpec parse(@Nullable Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue() >= 0 ? new SizeSpec(Kind.FIXED, number.intValue()) : null;
        String text = value.toString().trim().toLowerCase(Locale.ROOT);
        try {
            if (text.equals("fit")) return FIT;
            if (text.equals("grow")) return GROW;
            if (text.endsWith("%")) {
                int percent = Integer.parseInt(text.substring(0, text.length() - 1).trim());
                return percent >= 0 && percent <= 100 ? new SizeSpec(Kind.PERCENT, percent) : null;
            }
            int pixels = Integer.parseInt(text);
            return pixels >= 0 ? new SizeSpec(Kind.FIXED, pixels) : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * @return The value as it is written in a configuration.
     */
    public @NotNull Object serialize() {
        return switch (this.kind) {
            case FIXED -> this.value;
            case FIT -> "fit";
            case GROW -> "grow";
            case PERCENT -> this.value + "%";
        };
    }
}
