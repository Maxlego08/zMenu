package fr.maxlego08.menu.hooks.canvas;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoGlyphProvider;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import net.momirealms.craftengine.bukkit.api.CraftEngineImages;
import net.momirealms.craftengine.core.font.BitmapImage;
import net.momirealms.craftengine.core.font.Image;
import net.momirealms.craftengine.core.font.ReferenceImage;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * The images of CraftEngine, referenced as {@code craftengine:<namespace>:<id>} and drawn whole, every row of
 * a grid image under the previous one. One row or one cell of a grid is drawn alone with its row and column,
 * counted from 0: {@code craftengine:default:emojis:1} is row 1, {@code craftengine:default:emojis:1:2} is
 * row 1, column 2.
 * <p>
 * CraftEngine ships its own copy of Adventure, so nothing is read from it but numbers and strings.
 */
@AutoGlyphProvider
@RequiresPlugin("CraftEngine")
public final class CraftEngineGlyphProvider implements GlyphProvider {

    private final Map<Key, Optional<BufferedImage>> textures = new ConcurrentHashMap<>();

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("craftengine");
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("CraftEngine");
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        String[] parts = path.split(":");
        if (parts.length < 2 || parts.length > 4) return Optional.empty();
        Integer row = null;
        Integer column = null;
        try {
            if (parts.length >= 3) row = Integer.parseInt(parts[2]);
            if (parts.length == 4) column = Integer.parseInt(parts[3]);
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }

        Image image = CraftEngineImages.byId(net.momirealms.craftengine.core.util.Key.of(parts[0], parts[1]));
        if (image instanceof ReferenceImage reference) {
            row = reference.row();
            column = reference.col();
            image = reference.image();
        }
        if (!(image instanceof BitmapImage bitmap)) return Optional.empty();

        Key texture = textureKey(bitmap.file());
        if (texture == null) return Optional.empty();
        Optional<BufferedImage> optional = this.readTexture(texture);
        if (optional.isEmpty()) {
            Logger.info("The texture " + texture.asString() + " of the CraftEngine image " + parts[0] + ":" + parts[1] + " cannot be read, a canvas cannot measure it", Logger.LogType.WARNING);
            return Optional.empty();
        }
        BufferedImage source = optional.get();
        int columns = bitmap.columns();
        int rows = bitmap.rows();
        int cellWidth = source.getWidth() / columns;
        int cellHeight = source.getHeight() / rows;
        Key font = Key.key(bitmap.font().asString());
        int ascent = Math.min(bitmap.ascent(), bitmap.height());

        List<GlyphCell> cells = new ArrayList<>();
        if (row != null) {
            List<Integer> columnsToDraw = column != null ? List.of(column) : IntStream.range(0, columns).boxed().toList();
            for (int index : columnsToDraw) {
                if (!bitmap.isValidCoordinate(row, index)) return Optional.empty();
                cells.add(cell(bitmap, texture, source, font, row, index, cellWidth, cellHeight, ascent));
            }
        } else {
            for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
                for (int index = 0; index < columns; index++) cells.add(cell(bitmap, texture, source, font, rowIndex, index, cellWidth, cellHeight, ascent));
            }
        }
        cells.removeIf(cell -> cell.nativeChar() == null);
        if (cells.isEmpty()) return Optional.empty();
        return Optional.of(new ResolvedGlyph("craftengine:" + path, cells, false));
    }

    private static @NotNull GlyphCell cell(@NotNull BitmapImage bitmap, @NotNull Key texture, @NotNull BufferedImage source, @NotNull Key font,
                                           int row, int column, int cellWidth, int cellHeight, int ascent) {
        int codepoint = bitmap.codepointAt(row, column);
        int ink = TextureMetrics.inkWidth(source, column * cellWidth, row * cellHeight, cellWidth, cellHeight);
        return new GlyphCell(texture, bitmap.columns(), bitmap.rows(), column, row, cellWidth, cellHeight, bitmap.height(), ink, font,
                codepoint == 0 ? null : Character.toString(codepoint), ascent);
    }

    private static @Nullable Key textureKey(@NotNull String file) {
        String value = file.endsWith(".png") ? file : file + ".png";
        int separator = value.indexOf(':');
        String namespace = separator < 0 ? Key.MINECRAFT_NAMESPACE : value.substring(0, separator);
        String path = separator < 0 ? value : value.substring(separator + 1);
        if (!Key.parseableNamespace(namespace) || !Key.parseableValue(path)) return null;
        return Key.key(namespace, path);
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return this.textures.computeIfAbsent(texture, key -> {
            for (Pack pack : CraftEngine.instance().packManager().loadedPacks()) {
                if (!pack.enabled()) continue;
                for (Path folder : pack.resourcePackFolders()) {
                    Path file = folder.resolve("assets").resolve(key.namespace()).resolve("textures").resolve(key.value());
                    if (!Files.isRegularFile(file)) continue;
                    try {
                        BufferedImage image = TextureMetrics.read(Files.readAllBytes(file));
                        if (image != null) return Optional.of(image);
                    } catch (IOException ignored) {
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public void reload() {
        this.textures.clear();
    }
}
