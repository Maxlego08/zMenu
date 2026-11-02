package fr.maxlego08.menu.api.dialog.canvas;

import org.jetbrains.annotations.NotNull;

/**
 * Collects what the resource pack must contain for a canvas to be drawn.
 * <p>
 * A bitmap glyph has a single vertical position per font, so every position an image or a text is
 * drawn at needs its own copy in the pack. Each element declares here the positions it can be drawn
 * at, before the pack is generated.
 */
public interface CanvasVariants {

    /**
     * Declares that a glyph can be drawn at a vertical position.
     *
     * @param glyph The glyph.
     * @param y     The canvas y of its top edge, from 0.
     */
    void glyph(@NotNull ResolvedGlyph glyph, int y);

    /**
     * Declares that a text font can be drawn at a vertical position.
     *
     * @param font The name of the font in {@code resource-pack/fonts.yml}, or {@code default}.
     * @param y    The canvas y of the top of the text, from 0.
     */
    void text(@NotNull String font, int y);

    /**
     * Declares a click area. An area is an invisible glyph of its size, so every size and position needs
     * one.
     *
     * @param y      The canvas y of its top edge, from 0.
     * @param width  Its width.
     * @param height Its height, from 1 to 256.
     */
    void area(int y, int width, int height);

    /**
     * Resolves a glyph reference for an element that declares its variants.
     *
     * @param reference The reference, such as {@code nexo:menu_items}.
     * @return The glyph, or null if it cannot be resolved yet.
     */
    ResolvedGlyph resolve(@NotNull String reference);

    /**
     * @return The width of the canvas whose variants are collected, or 0 when it is not known. A canvas whose
     * width is a placeholder gives its default width.
     */
    default int canvasWidth() {
        return 0;
    }

    /**
     * @return The height of the canvas whose variants are collected, or 0 when it is not known.
     */
    default int canvasHeight() {
        return 0;
    }

    /**
     * Marks the start of what {@link CanvasElement#renderOverlay} draws, until {@link #endOverlay()}. The pack
     * places each element on a text line of its own (see {@link CanvasBuilder#line(int)}), one for its first
     * pass and one for its overlay pass, so it needs to know which images belong to which. Calls may nest.
     */
    default void beginOverlay() {
    }

    /**
     * Marks the end of what {@link CanvasElement#renderOverlay} draws, see {@link #beginOverlay()}.
     */
    default void endOverlay() {
    }
}
