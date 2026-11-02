package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * Measures the characters of a bitmap font provider the way the client does.
 */
public final class BitmapAdvances {

    private BitmapAdvances() {
    }

    /**
     * Adds the advance of every character of a bitmap provider. A character already measured by an
     * earlier provider keeps its advance, like the client gives it to the first provider that has it.
     *
     * @param provider The provider, with {@code chars}, {@code height} (default 8) and a texture.
     * @param image    The texture of the provider.
     * @param advances The advances, changed in place.
     */
    public static void measure(@NotNull JsonObject provider, @NotNull BufferedImage image, @NotNull Map<Integer, Double> advances) {
        List<int[]> rows = rows(provider.getAsJsonArray("chars"));
        if (rows.isEmpty()) return;
        int columns = rows.stream().mapToInt(row -> row.length).max().orElse(0);
        if (columns == 0) return;
        int cellWidth = image.getWidth() / columns;
        int cellHeight = image.getHeight() / rows.size();
        int height = provider.has("height") ? provider.get("height").getAsInt() : 8;

        for (int row = 0; row < rows.size(); row++) {
            int[] codepoints = rows.get(row);
            for (int column = 0; column < codepoints.length; column++) {
                int codepoint = codepoints[column];
                if (codepoint == 0 || advances.containsKey(codepoint)) continue;
                int ink = TextureMetrics.inkWidth(image, column * cellWidth, row * cellHeight, cellWidth, cellHeight);
                advances.put(codepoint, (double) GlyphCell.advance(ink, height, cellHeight));
            }
        }
    }

    /**
     * Adds the advances of a {@code space} provider.
     *
     * @param provider The provider.
     * @param advances The advances, changed in place.
     */
    public static void measureSpaces(@NotNull JsonObject provider, @NotNull Map<Integer, Double> advances) {
        JsonObject values = provider.getAsJsonObject("advances");
        if (values == null) return;
        for (String key : values.keySet()) {
            int codepoint = key.codePointAt(0);
            advances.putIfAbsent(codepoint, values.get(key).getAsDouble());
        }
    }

    private static @NotNull List<int[]> rows(JsonArray chars) {
        if (chars == null) return List.of();
        return chars.asList().stream().map(JsonElement::getAsString).map(row -> row.codePoints().toArray()).toList();
    }

    /**
     * Copies a bitmap provider with its glyphs moved down.
     *
     * @param provider The provider.
     * @param y        How many pixels to move the glyphs down.
     * @return The copy.
     */
    public static @NotNull JsonObject shifted(@NotNull JsonObject provider, int y) {
        JsonObject copy = provider.deepCopy();
        int ascent = provider.has("ascent") ? provider.get("ascent").getAsInt() : 7;
        copy.addProperty("ascent", ascent - y);
        return copy;
    }
}
