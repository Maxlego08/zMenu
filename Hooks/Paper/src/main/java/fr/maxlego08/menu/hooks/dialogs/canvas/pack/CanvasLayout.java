package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * What the last generated pack contains, to draw canvases with it.
 *
 * @param glyphs The character of every image cell variant, in the canvas font of its y.
 * @param texts  The text fonts generated at each y, as {@code font@y}.
 */
public record CanvasLayout(@NotNull Map<GlyphVariant, String> glyphs, @NotNull Set<String> texts) {

    public static final CanvasLayout EMPTY = new CanvasLayout(Map.of(), Set.of());

    public CanvasLayout {
        glyphs = Map.copyOf(glyphs);
        texts = Set.copyOf(texts);
    }

    /**
     * @param y The canvas y of the top of the glyphs.
     * @return The key of the canvas font holding the glyphs drawn at {@code y}.
     */
    public static @NotNull Key canvasFont(int y) {
        return Key.key("zmenu", "canvas/y" + y);
    }

    /**
     * @param variant An image cell variant.
     * @return Its character, or null if the pack does not contain it.
     */
    public @Nullable String glyph(@NotNull GlyphVariant variant) {
        return this.glyphs.get(variant);
    }

    /**
     * @param font The name of a text font.
     * @param y    The canvas y of the top of the text.
     * @return True if the pack contains the font at this position.
     */
    public boolean hasText(@NotNull String font, int y) {
        return this.texts.contains(textKey(font, y));
    }

    static @NotNull String textKey(@NotNull String font, int y) {
        return font + "@" + y;
    }
}
