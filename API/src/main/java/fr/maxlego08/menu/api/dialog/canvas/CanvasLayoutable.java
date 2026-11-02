package fr.maxlego08.menu.api.dialog.canvas;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * A canvas element that a {@code row} or {@code column} layout can place.
 * <p>
 * A layout asks for the size an element would like, then gives it a box. The size must not depend on the
 * player: a layout is computed once to generate the resource pack, where every vertical position must be
 * known, and again for each player, and both must give the same vertical positions.
 */
public interface CanvasLayoutable {

    /**
     * @param glyphs Resolves glyph references, to measure images.
     * @return The width the element would like, in GUI pixels.
     */
    int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs);

    /**
     * @param glyphs Resolves glyph references, to measure images.
     * @return The height the element would like, in GUI pixels.
     */
    int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs);

    /**
     * Places a copy of the element in a box. An element that cannot change its size, such as an image,
     * only takes the position.
     *
     * @param x      The x of the box.
     * @param y      The y of the box.
     * @param width  The width of the box.
     * @param height The height of the box.
     * @return The placed copy.
     */
    @NotNull
    CanvasElement placed(int x, int y, int width, int height);
}
