package fr.maxlego08.menu.api.dialog.canvas;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An image a canvas can draw, resolved from a reference such as {@code nexo:menu_items} by its
 * {@link GlyphProvider}.
 * <p>
 * Its cells are drawn in rows: cells of a grid texture keep their grid row and are placed by their column,
 * so a grid glyph keeps its shape even when some cells are empty. Cells of different textures, such as the
 * slices of a wide image, are drawn side by side.
 *
 * @param reference The reference the glyph was resolved from, as written in the configuration.
 * @param cells     The cells to draw, at least one.
 * @param animated  True if the owning plugin animates the glyph: a canvas can then only draw it where
 *                  the owning plugin draws it.
 */
public record ResolvedGlyph(@NotNull String reference, @NotNull List<GlyphCell> cells, boolean animated) {

    public ResolvedGlyph {
        if (cells.isEmpty()) throw new IllegalArgumentException("A glyph needs at least one cell: " + reference);
        cells = List.copyOf(cells);
    }

    /**
     * @return The cells grouped by row, from the top, each row in drawing order.
     */
    public @NotNull List<List<GlyphCell>> rows() {
        int first = this.cells.stream().mapToInt(GlyphCell::row).min().orElse(0);
        Map<Integer, List<GlyphCell>> rows = new TreeMap<>();
        for (GlyphCell cell : this.cells) rows.computeIfAbsent(cell.row() - first, row -> new ArrayList<>()).add(cell);
        List<List<GlyphCell>> result = new ArrayList<>();
        int last = rows.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        for (int row = 0; row <= last; row++) result.add(List.copyOf(rows.getOrDefault(row, List.of())));
        return result;
    }

    /**
     * @return The height of one row, which is the height its cells are drawn at.
     */
    public int rowHeight() {
        return this.cells.getFirst().height();
    }

    /**
     * Computes where the cells of a row are drawn, from the left edge of the glyph.
     *
     * @param row A row of {@link #rows()}.
     * @return The x of each cell, in the order of the row.
     */
    public int @NotNull [] cellOffsets(@NotNull List<GlyphCell> row) {
        int[] offsets = new int[row.size()];
        int firstColumn = this.cells.stream().filter(cell -> cell.gridColumns() > 1).mapToInt(GlyphCell::column).min().orElse(0);
        int cursor = 0;
        for (int index = 0; index < row.size(); index++) {
            GlyphCell cell = row.get(index);
            offsets[index] = cell.gridColumns() > 1 ? (cell.column() - firstColumn) * scaledWidth(cell) : cursor;
            cursor = offsets[index] + cell.advance() - 1;
        }
        return offsets;
    }

    private static int scaledWidth(@NotNull GlyphCell cell) {
        return (int) Math.round(cell.cellWidth() * ((double) cell.height() / cell.cellHeight()));
    }

    /**
     * @return True if every cell has a native glyph drawn at the same height, on a single row.
     */
    public boolean hasNativeGlyph() {
        if (this.rows().size() != 1) return false;
        int y = this.cells.getFirst().nativeY();
        for (GlyphCell cell : this.cells) {
            if (!cell.hasNativeGlyph() || cell.nativeY() != y) return false;
        }
        return true;
    }

    /**
     * @return The canvas y at which the owning plugin draws this glyph.
     */
    public int nativeY() {
        return this.cells.getFirst().nativeY();
    }

    /**
     * @return How far the widest row moves the cursor, including the extra pixel the client adds after
     * every bitmap glyph.
     */
    public int advance() {
        int advance = 0;
        for (List<GlyphCell> row : this.rows()) {
            if (row.isEmpty()) continue;
            int[] offsets = cellOffsets(row);
            int last = row.size() - 1;
            advance = Math.max(advance, offsets[last] + row.get(last).advance() + last);
        }
        return advance;
    }

    /**
     * @return The width of the drawn image, without the extra pixel the client adds after every glyph.
     */
    public int visualWidth() {
        int width = 0;
        for (List<GlyphCell> row : this.rows()) {
            if (row.isEmpty()) continue;
            int[] offsets = cellOffsets(row);
            int last = row.size() - 1;
            width = Math.max(width, offsets[last] + row.get(last).advance() - 1);
        }
        return width;
    }

    /**
     * @return The height of the glyph in GUI pixels.
     */
    public int height() {
        return this.rows().size() * this.rowHeight();
    }

    /**
     * @param height The height of the whole glyph; a glyph of several rows shares it between its rows.
     * @return A copy of this glyph drawn at another height, without its native glyphs.
     */
    public @NotNull ResolvedGlyph withHeight(int height) {
        int rowHeight = Math.max(1, Math.round(height / (float) this.rows().size()));
        return new ResolvedGlyph(this.reference, this.cells.stream().map(cell -> cell.withHeight(rowHeight)).toList(), this.animated);
    }
}
