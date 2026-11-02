package fr.maxlego08.menu.hooks.dialogs.canvas.glyph;

import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.CanvasPackGenerator;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuts an image of any size into a grid of equal tiles of at most 256 pixels, which is the largest glyph
 * the client draws.
 * <p>
 * The tiles are packed into one texture, so they are the cells of a single bitmap grid: a canvas draws each
 * row of tiles under the previous one and each tile at its column, and the image looks whole. Tiles are made
 * equal by adding transparent pixels on the right and at the bottom, at most one pixel per tile, and each
 * tile gets its top-right pixel made barely visible so the client measures it by its full width.
 */
public final class TileSheet {

    private TileSheet() {
    }

    /**
     * The texture holding the tiles, and the cells to draw.
     *
     * @param image The texture.
     * @param cells The tiles, row by row.
     */
    public record Result(@NotNull BufferedImage image, @NotNull List<GlyphCell> cells) {
    }

    /**
     * Cuts an image into tiles.
     *
     * @param source  The image.
     * @param texture The key the texture of the tiles is written under.
     * @return The tiles.
     */
    public static @NotNull Result tile(@NotNull BufferedImage source, @NotNull Key texture) {
        int max = CanvasPackGenerator.MAX_CELL_SIZE;
        int columns = Math.max(1, (int) Math.ceil(source.getWidth() / (double) max));
        int rows = Math.max(1, (int) Math.ceil(source.getHeight() / (double) max));
        int cellWidth = (int) Math.ceil(source.getWidth() / (double) columns);
        int cellHeight = (int) Math.ceil(source.getHeight() / (double) rows);

        BufferedImage sheet = TextureMetrics.copy(source, 0, 0, columns * cellWidth, rows * cellHeight);
        List<GlyphCell> cells = new ArrayList<>(columns * rows);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                int markX = column * cellWidth + cellWidth - 1;
                int markY = row * cellHeight;
                if ((sheet.getRGB(markX, markY) >>> 24) == 0) sheet.setRGB(markX, markY, 0x01000000);
                cells.add(new GlyphCell(texture, columns, rows, column, row, cellWidth, cellHeight, cellHeight, cellWidth, null, null, 0));
            }
        }
        return new Result(sheet, cells);
    }
}
