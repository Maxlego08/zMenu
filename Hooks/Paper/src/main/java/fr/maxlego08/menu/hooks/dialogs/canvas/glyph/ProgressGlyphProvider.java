package fr.maxlego08.menu.hooks.dialogs.canvas.glyph;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

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
 * Progress bars: a texture of {@code resource-pack/textures} cut to the width of its filled part. Each
 * width is its own texture, so a bar has one texture per step it can show.
 * <p>
 * Referenced as {@code progress:<texture>@<filled width>}, which {@link #reference} writes. A width at
 * least the width of the texture gives the whole texture.
 */
public final class ProgressGlyphProvider implements GlyphProvider {

    private static final Pattern REFERENCE = Pattern.compile("([a-z0-9_./-]+)@(\\d+)");

    private final Path folder;
    private final Map<String, Optional<ResolvedGlyph>> bars = new ConcurrentHashMap<>();
    private final Map<String, Optional<BufferedImage>> sources = new ConcurrentHashMap<>();
    private final Map<Key, BufferedImage> slices = new ConcurrentHashMap<>();

    public ProgressGlyphProvider(@NotNull Path resourcePackFolder) {
        this.folder = resourcePackFolder.resolve("textures");
    }

    /**
     * @return The glyph reference of a bar filled over {@code filled} pixels.
     */
    public static @NotNull String reference(@NotNull String texture, int filled) {
        return "progress:" + texture + "@" + filled;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("progress");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        return this.bars.computeIfAbsent(path, this::load);
    }

    private @NotNull Optional<ResolvedGlyph> load(@NotNull String path) {
        Matcher matcher = REFERENCE.matcher(path);
        if (!matcher.matches() || path.contains("..")) return Optional.empty();
        String texture = matcher.group(1);
        BufferedImage source = this.sources.computeIfAbsent(texture, this::read).orElse(null);
        if (source == null) return Optional.empty();
        int filled = (int) Math.min(source.getWidth(), Long.parseLong(matcher.group(2)));
        if (filled < 1) return Optional.empty();

        Key key = Key.key(ZMenuKeys.NAMESPACE, "font/canvas/progress/" + texture + "_" + filled + ".png");
        TileSheet.Result tiles = TileSheet.tile(TextureMetrics.copy(source, 0, 0, filled, source.getHeight()), key);
        this.slices.put(key, tiles.image());
        return Optional.of(new ResolvedGlyph("progress:" + path, tiles.cells(), false));
    }

    private @NotNull Optional<BufferedImage> read(@NotNull String texture) {
        Path file = this.folder.resolve(texture + ".png");
        if (!Files.isRegularFile(file)) {
            Logger.info("The progress texture " + file + " does not exist", Logger.LogType.WARNING);
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(TextureMetrics.read(Files.readAllBytes(file)));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return Optional.ofNullable(this.slices.get(texture));
    }

    @Override
    public void reload() {
        this.bars.clear();
        this.sources.clear();
        this.slices.clear();
    }
}
