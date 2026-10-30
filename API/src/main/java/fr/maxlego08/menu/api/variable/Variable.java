package fr.maxlego08.menu.api.variable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * The value of a variable at one moment. Variables never change in place: a write produces a new
 * {@code Variable}.
 *
 * @param ref       which variable
 * @param text      the value as text; for a number, its plain form without trailing zeros
 * @param number    the value as a number, or null when it is not a number
 * @param expiresAt when it expires, in milliseconds since the epoch, or 0 when it never does
 */
@ApiStatus.Experimental
public record Variable(@NotNull VariableRef ref, @NotNull String text, @Nullable BigDecimal number, long expiresAt) {

    /**
     * Builds a variable from a text, kept as a number when it reads as one ({@code 10},
     * {@code -2.5}).
     */
    public static @NotNull Variable of(@NotNull VariableRef ref, @NotNull String text, long expiresAt) {
        BigDecimal number = parseNumber(text);
        return number == null ? new Variable(ref, text, null, expiresAt) : ofNumber(ref, number, expiresAt);
    }

    public static @NotNull Variable ofNumber(@NotNull VariableRef ref, @NotNull BigDecimal number, long expiresAt) {
        return new Variable(ref, format(number), number, expiresAt);
    }

    /**
     * @return the number a text holds, or null. Commas are read as decimal points.
     */
    public static @Nullable BigDecimal parseNumber(@Nullable String text) {
        if (text == null) return null;
        String trimmed = text.trim().replace(",", ".");
        if (trimmed.isEmpty()) return null;
        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * @return a number without trailing zeros or exponent: {@code 15}, {@code 2.5}
     */
    public static @NotNull String format(@NotNull BigDecimal number) {
        BigDecimal stripped = number.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0).toPlainString() : stripped.toPlainString();
    }

    public @NotNull Optional<BigDecimal> asNumber() {
        return Optional.ofNullable(this.number);
    }

    public boolean isExpired() {
        return this.expiresAt != 0 && System.currentTimeMillis() > this.expiresAt;
    }
}
