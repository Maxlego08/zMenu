package fr.maxlego08.menu.hooks.canvas;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.AnimatedGlyph;
import com.nexomc.nexo.glyphs.Glyph;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoGlyphProvider;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The glyphs of Nexo, referenced as {@code nexo:<glyph id>}, drawn whole, every row of a multi-bitmap glyph
 * under the previous one. One cell of a multi-bitmap glyph is drawn alone with its number, counted from 1
 * left to right and top to bottom: {@code nexo:icons:2}.
 */
@AutoGlyphProvider
@RequiresPlugin("Nexo")
public final class NexoGlyphProvider implements GlyphProvider {

    private final Map<Key, Optional<BufferedImage>> textures = new ConcurrentHashMap<>();

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("nexo");
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("Nexo");
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        String id = path;
        Integer index = null;
        int separator = path.lastIndexOf(':');
        if (separator > 0) {
            try {
                index = Integer.parseInt(path.substring(separator + 1));
                id = path.substring(0, separator);
            } catch (NumberFormatException ignored) {
            }
        }

        Glyph glyph = NexoPlugin.instance().fontManager().glyphFromID(id);
        if (glyph == null) return Optional.empty();

        List<int[]> rows = glyph.getUnicodes().stream().map(row -> row.codePoints().toArray()).toList();
        if (rows.isEmpty()) return Optional.empty();
        int columns = rows.stream().mapToInt(row -> row.length).max().orElse(1);

        Key texture = glyph.getTexture();
        Optional<BufferedImage> optional = this.readTexture(texture);
        if (optional.isEmpty()) {
            Logger.info("The texture " + texture.asString() + " of the Nexo glyph " + id + " cannot be read, a canvas cannot measure it", Logger.LogType.WARNING);
            return Optional.empty();
        }
        BufferedImage image = optional.get();
        int cellWidth = image.getWidth() / columns;
        int cellHeight = image.getHeight() / rows.size();
        int ascent = Math.min(glyph.getAscent(), glyph.getHeight());

        List<GlyphCell> cells = new ArrayList<>();
        if (index != null) {
            int flat = index - 1;
            int row = flat / columns;
            int column = flat % columns;
            if (flat < 0 || row >= rows.size() || column >= rows.get(row).length) return Optional.empty();
            cells.add(this.cell(glyph, texture, image, columns, rows.size(), column, row, cellWidth, cellHeight, ascent, rows.get(row)[column]));
        } else {
            for (int row = 0; row < rows.size(); row++) {
                int[] codepoints = rows.get(row);
                for (int column = 0; column < codepoints.length; column++) {
                    if (codepoints[column] == 0) continue;
                    cells.add(this.cell(glyph, texture, image, columns, rows.size(), column, row, cellWidth, cellHeight, ascent, codepoints[column]));
                }
            }
            if (cells.isEmpty()) return Optional.empty();
        }
        return Optional.of(new ResolvedGlyph("nexo:" + path, cells, glyph instanceof AnimatedGlyph));
    }

    private @NotNull GlyphCell cell(@NotNull Glyph glyph, @NotNull Key texture, @NotNull BufferedImage image, int columns, int rows, int column, int row,
                                    int cellWidth, int cellHeight, int ascent, int codepoint) {
        int ink = TextureMetrics.inkWidth(image, column * cellWidth, row * cellHeight, cellWidth, cellHeight);
        return new GlyphCell(texture, columns, rows, column, row, cellWidth, cellHeight, glyph.getHeight(), ink, glyph.getFont(), Character.toString(codepoint), ascent);
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return this.textures.computeIfAbsent(texture, NexoPackAccess::readTexture);
    }

    @Override
    public void reload() {
        this.textures.clear();
    }
}
