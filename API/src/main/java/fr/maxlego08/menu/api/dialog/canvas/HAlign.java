package fr.maxlego08.menu.api.dialog.canvas;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * The horizontal alignment of text inside its box.
 */
public enum HAlign {

    LEFT,
    CENTER,
    RIGHT;

    /**
     * Computes where a text starts inside its box.
     *
     * @param boxWidth  The width of the box.
     * @param textWidth The width of the text.
     * @return The offset from the left edge of the box.
     */
    public double offset(double boxWidth, double textWidth) {
        return switch (this) {
            case LEFT -> 0;
            case CENTER -> Math.floor((boxWidth - textWidth) / 2);
            case RIGHT -> boxWidth - textWidth;
        };
    }

    /**
     * @param value The name of the alignment, in any case.
     * @return The alignment, {@link #LEFT} for an unknown value.
     */
    public static @NotNull HAlign parse(String value) {
        if (value == null) return LEFT;
        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return LEFT;
        }
    }
}
