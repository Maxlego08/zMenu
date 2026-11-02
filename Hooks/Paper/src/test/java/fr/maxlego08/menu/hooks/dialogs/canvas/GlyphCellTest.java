package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GlyphCellTest {

    @Test
    public void advanceFollowsTheClient() {
        assertEquals(17, GlyphCell.advance(16, 16, 16));
        assertEquals(1, GlyphCell.advance(0, 16, 16));
        assertEquals(25, GlyphCell.advance(16, 24, 16));
        assertEquals(6, GlyphCell.advance(5, 8, 8));
        assertEquals(4, GlyphCell.advance(5, 4, 8));
    }

    private static GlyphCell grid(int column, int row) {
        return new GlyphCell(Key.key("test", "grid.png"), 3, 2, column, row, 8, 8, 8, 8, Key.key("test", "font"), "x", 7);
    }

    @Test
    public void aGridGlyphKeepsItsShape() {
        ResolvedGlyph glyph = new ResolvedGlyph("test:grid", List.of(grid(0, 0), grid(2, 0), grid(1, 1)), false);
        List<List<GlyphCell>> rows = glyph.rows();
        assertEquals(2, rows.size());
        assertEquals(2, rows.getFirst().size());
        assertEquals(16, glyph.cellOffsets(rows.getFirst())[1]);
        assertEquals(8, glyph.cellOffsets(rows.get(1))[0]);
        assertEquals(24, glyph.visualWidth());
        assertEquals(16, glyph.height());
        assertFalse(glyph.hasNativeGlyph());
    }

    @Test
    public void slicesAreDrawnSideBySide() {
        GlyphCell first = new GlyphCell(Key.key("test", "a_s0.png"), 1, 1, 0, 0, 256, 10, 10, 256, null, null, 0);
        GlyphCell second = new GlyphCell(Key.key("test", "a_s1.png"), 1, 1, 0, 0, 44, 10, 10, 44, null, null, 0);
        ResolvedGlyph glyph = new ResolvedGlyph("test:a", List.of(first, second), false);
        assertEquals(1, glyph.rows().size());
        assertEquals(256, glyph.cellOffsets(glyph.rows().getFirst())[1]);
        assertEquals(300, glyph.visualWidth());
    }

    @Test
    public void nativeGlyphNeedsEveryCellAtTheSameHeight() {
        GlyphCell first = new GlyphCell(Key.key("test", "a.png"), 2, 1, 0, 0, 8, 8, 8, 8, Key.key("test", "font"), "a", 7);
        GlyphCell second = new GlyphCell(Key.key("test", "a.png"), 2, 1, 1, 0, 8, 8, 8, 8, Key.key("test", "font"), "b", 7);
        ResolvedGlyph glyph = new ResolvedGlyph("test:a", List.of(first, second), false);
        assertTrue(glyph.hasNativeGlyph());
        assertEquals(0, glyph.nativeY());
        assertEquals(18, glyph.advance());
        assertFalse(glyph.withHeight(16).hasNativeGlyph());
    }
}
