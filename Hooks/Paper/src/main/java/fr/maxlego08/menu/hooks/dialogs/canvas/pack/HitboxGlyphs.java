package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The invisible glyphs carrying the click and hover of canvas areas.
 * <p>
 * From 1.21.11 the client finds what is under the mouse by the rectangle of each drawn glyph, and keeps
 * the last one with a click event. An area is therefore a bitmap glyph of its size, drawn after everything
 * else. Its texture is empty except for its top-right pixel, given the smallest alpha that is not fully
 * transparent: the client does not draw it, but measures the glyph by its full width, and counts it as
 * visible so the line takes clicks over the whole area.
 */
public final class HitboxGlyphs implements GlyphProvider {

    public static final HitboxGlyphs INSTANCE = new HitboxGlyphs();

    private static final String FOLDER = "font/canvas/hitbox/";
    private static final Pattern NAME = Pattern.compile(Pattern.quote(FOLDER) + "(\\d+)x(\\d+)\\.png");

    private HitboxGlyphs() {
    }

    /**
     * Splits an area into the cells drawing it: slices of at most 256 pixels wide.
     *
     * @param width  The width of the area.
     * @param height The height of the area, at most 256.
     * @return The cells, from left to right.
     */
    public static @NotNull List<GlyphCell> cells(int width, int height) {
        List<GlyphCell> cells = new ArrayList<>();
        for (int x = 0; x < width; x += CanvasPackGenerator.MAX_CELL_SIZE) {
            int slice = Math.min(CanvasPackGenerator.MAX_CELL_SIZE, width - x);
            Key texture = Key.key("zmenu", FOLDER + slice + "x" + height + ".png");
            cells.add(new GlyphCell(texture, 1, 1, 0, 0, slice, height, height, slice, null, null, 0));
        }
        return cells;
    }

    @Override
    public @NotNull Key key() {
        return Key.key("zmenu", "hitbox");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        return Optional.empty();
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        Matcher matcher = NAME.matcher(texture.value());
        if (!texture.namespace().equals("zmenu") || !matcher.matches()) return Optional.empty();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        if (width < 1 || height < 1 || width > CanvasPackGenerator.MAX_CELL_SIZE || height > CanvasPackGenerator.MAX_CELL_SIZE) return Optional.empty();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(width - 1, 0, 0x01000000);
        return Optional.of(image);
    }
}
