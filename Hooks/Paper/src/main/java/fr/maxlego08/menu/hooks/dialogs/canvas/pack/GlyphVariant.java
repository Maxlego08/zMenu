package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import org.jetbrains.annotations.NotNull;

/**
 * One cell of an image drawn at one vertical position, which is one character of a canvas font.
 *
 * @param cell The cell, without its native glyph.
 * @param y    The canvas y of its top edge.
 */
public record GlyphVariant(@NotNull GlyphCell cell, int y) {

    public GlyphVariant {
        cell = cell.withHeight(cell.height());
    }

    /**
     * @return The identity of the cell in the codepoint lock, shared by every vertical position.
     */
    public @NotNull String lockKey() {
        return this.cell.texture().asString() + "#" + this.cell.column() + "," + this.cell.row() + "@" + this.cell.height();
    }
}
