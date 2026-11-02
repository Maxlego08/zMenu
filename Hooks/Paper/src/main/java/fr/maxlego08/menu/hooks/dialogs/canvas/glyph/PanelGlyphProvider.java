package fr.maxlego08.menu.hooks.dialogs.canvas.glyph;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Panels: a texture of {@code resource-pack/textures} stretched to any size by nine-slice. Its corners
 * are kept, its edges and middle are repeated. The panel is drawn into its own texture when the pack is
 * generated, so it costs one texture per size; a panel over 256 pixels in either direction is cut into tiles,
 * see {@link TileSheet}.
 * <p>
 * Referenced as {@code panel:<texture>@<width>x<height>@<border>}, which {@link #reference} writes.
 */
public final class PanelGlyphProvider implements GlyphProvider {

    private static final Pattern REFERENCE = Pattern.compile("([a-z0-9_./-]+)@(\\d+)x(\\d+)@(\\d+)");

    private final Path folder;
    private final Map<String, Optional<ResolvedGlyph>> panels = new ConcurrentHashMap<>();
    private final Map<Key, BufferedImage> slices = new ConcurrentHashMap<>();

    public PanelGlyphProvider(@NotNull Path resourcePackFolder) {
        this.folder = resourcePackFolder.resolve("textures");
    }

    /**
     * @return The glyph reference of a panel.
     */
    public static @NotNull String reference(@NotNull String texture, int width, int height, int border) {
        return "panel:" + texture + "@" + width + "x" + height + "@" + border;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("panel");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        return this.panels.computeIfAbsent(path, this::load);
    }

    private @NotNull Optional<ResolvedGlyph> load(@NotNull String path) {
        Matcher matcher = REFERENCE.matcher(path);
        if (!matcher.matches() || path.contains("..")) return Optional.empty();
        String texture = matcher.group(1);
        int width = Integer.parseInt(matcher.group(2));
        int height = Integer.parseInt(matcher.group(3));
        int border = Integer.parseInt(matcher.group(4));

        BufferedImage source = this.read(texture);
        if (source == null) return Optional.empty();
        if (border * 2 > Math.min(source.getWidth(), source.getHeight())) {
            Logger.info("The panel texture " + texture + " is too small for a border of " + border, Logger.LogType.WARNING);
            return Optional.empty();
        }
        BufferedImage panel = nineSlice(source, width, height, border);

        Key key = Key.key(ZMenuKeys.NAMESPACE, "font/canvas/panel/" + texture + "_" + width + "x" + height + "_b" + border + ".png");
        TileSheet.Result tiles = TileSheet.tile(panel, key);
        this.slices.put(key, tiles.image());
        return Optional.of(new ResolvedGlyph("panel:" + path, tiles.cells(), false));
    }

    private @Nullable BufferedImage read(@NotNull String texture) {
        Path file = this.folder.resolve(texture + ".png");
        if (!Files.isRegularFile(file)) {
            Logger.info("The panel texture " + file + " does not exist", Logger.LogType.WARNING);
            return null;
        }
        try {
            return TextureMetrics.read(Files.readAllBytes(file));
        } catch (IOException exception) {
            return null;
        }
    }

    /**
     * Stretches a texture by nine-slice: the corners of {@code border} pixels are copied, the edges and the
     * middle are repeated.
     *
     * @param source The texture.
     * @param width  The width of the result.
     * @param height The height of the result.
     * @param border The size of the corners.
     * @return The stretched texture.
     */
    public static @NotNull BufferedImage nineSlice(@NotNull BufferedImage source, int width, int height, int border) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            int sourceY = map(y, height, source.getHeight(), border);
            for (int x = 0; x < width; x++) {
                result.setRGB(x, y, source.getRGB(map(x, width, source.getWidth(), border), sourceY));
            }
        }
        return result;
    }

    private static int map(int position, int size, int sourceSize, int border) {
        if (position < border) return position;
        if (position >= size - border) return sourceSize - (size - position);
        int middle = sourceSize - border * 2;
        if (middle <= 0) return Math.min(border, sourceSize - 1);
        return border + (position - border) % middle;
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return Optional.ofNullable(this.slices.get(texture));
    }

    @Override
    public void reload() {
        this.panels.clear();
        this.slices.clear();
    }
}
