package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * The invisible characters that move the cursor of a canvas line, in a {@code space} font provider.
 * <p>
 * Whole pixels are written with powers of two from 1 to 1024, positive and negative, so any move takes
 * at most a dozen characters. Quarter pixels are only used after text whose width is not a whole number.
 * The characters sit at the end of plane 15, away from what Nexo, ItemsAdder and CraftEngine allocate.
 */
public final class SpaceFont {

    public static final Key KEY = Key.key("zmenu", "space");

    private static final int POSITIVE_BASE = 0xFFF00;
    private static final int NEGATIVE_BASE = 0xFFF10;
    private static final int PLUS_HALF = 0xFFF20;
    private static final int MINUS_HALF = 0xFFF21;
    private static final int PLUS_QUARTER = 0xFFF22;
    private static final int MINUS_QUARTER = 0xFFF23;
    private static final int POWERS = 11;
    private static final int LARGEST = 1 << (POWERS - 1);

    public static final int FIRST_RESERVED = 0xFFF00;

    private SpaceFont() {
    }

    /**
     * Writes a cursor move, rounded to the nearest quarter of a pixel.
     *
     * @param pixels The distance, negative to move left.
     * @return The characters, empty for a move of zero.
     */
    public static @NotNull String encode(double pixels) {
        double quarters = Math.round(pixels * 4) / 4.0;
        if (quarters == 0) return "";
        boolean negative = quarters < 0;
        double absolute = Math.abs(quarters);
        long whole = (long) Math.floor(absolute);
        double fraction = absolute - whole;

        StringBuilder builder = new StringBuilder();
        while (whole > 0) {
            int power = Math.min(highestPower(whole), POWERS - 1);
            builder.appendCodePoint((negative ? NEGATIVE_BASE : POSITIVE_BASE) + power);
            whole -= 1L << power;
        }
        if (fraction >= 0.5) {
            builder.appendCodePoint(negative ? MINUS_HALF : PLUS_HALF);
            fraction -= 0.5;
        }
        if (fraction >= 0.25) {
            builder.appendCodePoint(negative ? MINUS_QUARTER : PLUS_QUARTER);
        }
        return builder.toString();
    }

    private static int highestPower(long value) {
        if (value >= LARGEST) return POWERS - 1;
        return 63 - Long.numberOfLeadingZeros(value);
    }

    /**
     * @return The {@code space} provider holding every character {@link #encode(double)} writes.
     */
    public static @NotNull JsonObject provider() {
        JsonObject advances = new JsonObject();
        for (int power = 0; power < POWERS; power++) {
            advances.addProperty(Character.toString(POSITIVE_BASE + power), 1 << power);
        }
        for (int power = 0; power < POWERS; power++) {
            advances.addProperty(Character.toString(NEGATIVE_BASE + power), -(1 << power));
        }
        advances.addProperty(Character.toString(PLUS_HALF), 0.5);
        advances.addProperty(Character.toString(MINUS_HALF), -0.5);
        advances.addProperty(Character.toString(PLUS_QUARTER), 0.25);
        advances.addProperty(Character.toString(MINUS_QUARTER), -0.25);

        JsonObject provider = new JsonObject();
        provider.addProperty("type", "space");
        provider.add("advances", advances);
        return provider;
    }
}
