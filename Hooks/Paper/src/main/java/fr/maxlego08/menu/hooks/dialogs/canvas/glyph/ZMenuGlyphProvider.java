package fr.maxlego08.menu.hooks.dialogs.canvas.glyph;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * The PNG files of {@code resource-pack/textures}, referenced as {@code zmenu:<path>} or just
 * {@code <path>}, without {@code .png}.
 * <p>
 * An image wider or higher than 256 pixels is cut into a grid of tiles drawn as one image, see
 * {@link TileSheet}.
 */
public final class ZMenuGlyphProvider implements GlyphProvider {

    private static final Pattern PATH = Pattern.compile("[a-z0-9_./-]+");

    private final Path folder;
    private final Map<String, Optional<Source>> sources = new ConcurrentHashMap<>();
    private final Map<Key, BufferedImage> slices = new ConcurrentHashMap<>();

    public ZMenuGlyphProvider(@NotNull Path resourcePackFolder) {
        this.folder = resourcePackFolder.resolve("textures");
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key(ZMenuKeys.NAMESPACE);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        Optional<Source> source = this.sources.computeIfAbsent(path, this::load);
        return source.map(value -> new ResolvedGlyph(ZMenuKeys.NAMESPACE + ":" + path, value.cells(), false));
    }

    private @NotNull Optional<Source> load(@NotNull String path) {
        if (!PATH.matcher(path).matches() || path.contains("..")) {
            Logger.info("The texture path " + path + " may only hold lowercase letters, digits, _, -, . and /", Logger.LogType.WARNING);
            return Optional.empty();
        }
        Path file = this.folder.resolve(path + ".png");
        if (!Files.isRegularFile(file)) return Optional.empty();

        BufferedImage image;
        try {
            image = TextureMetrics.read(Files.readAllBytes(file));
        } catch (IOException exception) {
            image = null;
        }
        if (image == null) {
            Logger.info("The texture " + file + " is not a PNG", Logger.LogType.WARNING);
            return Optional.empty();
        }
        Key texture = Key.key(ZMenuKeys.NAMESPACE, "font/canvas/" + path + ".png");
        TileSheet.Result tiles = TileSheet.tile(image, texture);
        this.slices.put(texture, tiles.image());
        return Optional.of(new Source(tiles.cells()));
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return Optional.ofNullable(this.slices.get(texture));
    }

    @Override
    public void reload() {
        this.sources.clear();
        this.slices.clear();
    }

    private record Source(@NotNull List<GlyphCell> cells) {
    }
}
