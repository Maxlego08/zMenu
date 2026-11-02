package fr.maxlego08.menu.hooks.dialogs.canvas.glyph;

import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.CanvasTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PanelAndProgressGlyphTest {

    @TempDir
    Path folder;

    @BeforeAll
    static void logger() {
        CanvasTestSupport.installLogger();
    }

    private void texture(String name, BufferedImage image) throws IOException {
        Path file = this.folder.resolve("textures").resolve(name + ".png");
        Files.createDirectories(file.getParent());
        ImageIO.write(image, "png", file.toFile());
    }

    private static BufferedImage frame() {
        BufferedImage image = new BufferedImage(6, 6, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                boolean corner = (x < 2 || x >= 4) && (y < 2 || y >= 4);
                image.setRGB(x, y, corner ? 0xFFFF0000 : 0xFF0000FF);
            }
        }
        return image;
    }

    @Test
    public void nineSliceKeepsTheCornersAndRepeatsTheRest() {
        BufferedImage panel = PanelGlyphProvider.nineSlice(frame(), 20, 10, 2);
        assertEquals(20, panel.getWidth());
        assertEquals(10, panel.getHeight());
        assertEquals(0xFFFF0000, panel.getRGB(0, 0));
        assertEquals(0xFFFF0000, panel.getRGB(19, 9));
        assertEquals(0xFFFF0000, panel.getRGB(18, 0));
        assertEquals(0xFF0000FF, panel.getRGB(10, 5));
        assertEquals(0xFF0000FF, panel.getRGB(2, 0));
    }

    @Test
    public void aWidePanelIsTiledAndMeasuredByItsFullWidth() throws IOException {
        this.texture("panels/frame", frame());
        PanelGlyphProvider provider = new PanelGlyphProvider(this.folder);
        Optional<ResolvedGlyph> glyph = provider.resolve("panels/frame@300x40@2");
        assertTrue(glyph.isPresent());
        assertEquals(2, glyph.get().cells().size());
        assertEquals(150, glyph.get().cells().getFirst().inkWidth());
        assertEquals(300, glyph.get().visualWidth());
        GlyphCell first = glyph.get().cells().getFirst();
        assertTrue(provider.readTexture(first.texture()).isPresent());
    }

    @Test
    public void aTallPanelIsTiledInRows() throws IOException {
        this.texture("panels/frame", frame());
        PanelGlyphProvider provider = new PanelGlyphProvider(this.folder);
        ResolvedGlyph glyph = provider.resolve("panels/frame@300x600@2").orElseThrow();
        assertEquals(6, glyph.cells().size());
        assertEquals(3, glyph.rows().size());
        assertEquals(200, glyph.rowHeight());
        assertEquals(600, glyph.height());
        assertEquals(300, glyph.visualWidth());
        assertTrue(glyph.cells().stream().allMatch(cell -> cell.cellWidth() <= 256 && cell.cellHeight() <= 256));

        BufferedImage sheet = provider.readTexture(glyph.cells().getFirst().texture()).orElseThrow();
        assertEquals(300, sheet.getWidth());
        assertEquals(600, sheet.getHeight());
        assertEquals(0xFFFF0000, sheet.getRGB(0, 599));
    }

    @Test
    public void aBigTextureIsTiledAndScaledAsAWhole() throws IOException {
        this.texture("art/big", new BufferedImage(301, 513, BufferedImage.TYPE_INT_ARGB));
        ZMenuGlyphProvider provider = new ZMenuGlyphProvider(this.folder);
        ResolvedGlyph glyph = provider.resolve("art/big").orElseThrow();
        assertEquals(2 * 3, glyph.cells().size());
        assertEquals(151, glyph.cells().getFirst().cellWidth());
        assertEquals(171, glyph.cells().getFirst().cellHeight());
        assertEquals(302, glyph.visualWidth());
        assertEquals(513, glyph.height());
        assertEquals(255, glyph.withHeight(256).height());
    }

    @Test
    public void aProgressBarIsCutToItsFilledWidth() throws IOException {
        BufferedImage bar = new BufferedImage(50, 5, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 50; x++) bar.setRGB(x, 2, 0xFF00FF00);
        this.texture("bars/green", bar);
        ProgressGlyphProvider provider = new ProgressGlyphProvider(this.folder);

        assertEquals(20, provider.resolve("bars/green@20").orElseThrow().cells().getFirst().cellWidth());
        assertEquals(50, provider.resolve("bars/green@" + Integer.MAX_VALUE).orElseThrow().cells().getFirst().cellWidth());
        assertTrue(provider.resolve("bars/green@0").isEmpty());
        assertTrue(provider.resolve("../green@10").isEmpty());
    }
}
