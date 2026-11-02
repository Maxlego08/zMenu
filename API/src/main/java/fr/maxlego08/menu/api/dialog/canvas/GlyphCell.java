package fr.maxlego08.menu.api.dialog.canvas;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * One cell of a bitmap texture that a canvas can draw: a whole image, or one cell of a grid of images
 * sharing a texture.
 * <p>
 * The client draws a bitmap glyph scaled by {@code height / cellHeight}, with its top edge at
 * {@code 7 - ascent} pixels below the top of its line, and moves the cursor by
 * {@code (int) (0.5 + inkWidth * height / cellHeight) + 1} pixels.
 *
 * @param texture      The texture file, as the resource pack names it ({@code namespace:path.png}).
 * @param gridColumns  The number of cells in a row of the texture.
 * @param gridRows     The number of rows of cells in the texture.
 * @param column       The column of this cell, from 0.
 * @param row          The row of this cell, from 0.
 * @param cellWidth    The width of one cell in texture pixels.
 * @param cellHeight   The height of one cell in texture pixels.
 * @param height       The height the cell is drawn at, in GUI pixels.
 * @param inkWidth     The width of the cell up to its rightmost column holding a pixel that is not fully transparent.
 * @param nativeFont   The font in which the owning plugin already draws this cell, or {@code null}.
 * @param nativeChar   The character of this cell in {@link #nativeFont()}, or {@code null}.
 * @param nativeAscent The ascent of the cell in {@link #nativeFont()}, ignored without a native font.
 */
public record GlyphCell(@NotNull Key texture, int gridColumns, int gridRows, int column, int row, int cellWidth, int cellHeight,
                        int height, int inkWidth, @Nullable Key nativeFont, @Nullable String nativeChar, int nativeAscent) {

    /**
     * @return The distance in pixels the client moves the cursor after drawing this cell at its height.
     */
    public int advance() {
        return advance(this.inkWidth, this.height, this.cellHeight);
    }

    /**
     * @return True if the owning plugin already draws this cell in a font a canvas can reuse.
     */
    public boolean hasNativeGlyph() {
        return this.nativeFont != null && this.nativeChar != null;
    }

    /**
     * @return The canvas y at which the native glyph is drawn, measured from the top of the first line.
     */
    public int nativeY() {
        return 7 - this.nativeAscent;
    }

    /**
     * Computes a bitmap glyph's advance the way the client does.
     *
     * @param inkWidth   The width of the cell up to its rightmost visible column, in texture pixels.
     * @param height     The height the cell is drawn at.
     * @param cellHeight The height of the cell in texture pixels.
     * @return The advance in GUI pixels.
     */
    public static int advance(int inkWidth, int height, int cellHeight) {
        if (cellHeight <= 0) return 1;
        return (int) (0.5 + inkWidth * ((double) height / cellHeight)) + 1;
    }

    /**
     * @return A copy of this cell drawn at another height, without its native glyph.
     */
    public @NotNull GlyphCell withHeight(int height) {
        return new GlyphCell(this.texture, this.gridColumns, this.gridRows, this.column, this.row, this.cellWidth, this.cellHeight,
                height, this.inkWidth, null, null, 0);
    }
}
