package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.CanvasTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextFonts;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Turns what the canvases need into the files of the {@code zmenu} namespace of a resource pack.
 * <p>
 * Every vertical position gets its own canvas font ({@code zmenu:canvas/y<n>}) holding the image cells
 * drawn at that position, and every text font gets a copy per position ({@code zmenu:text/<font>/y<n>}).
 * A cell keeps the same character in every canvas font, given by the {@link CodepointLock}.
 * <p>
 * Textures of the {@code zmenu} namespace are written to the pack. Other textures already are in the
 * final pack, from the client or from the plugin owning them, so the fonts only point at them. A cell
 * placed so high that its ascent would exceed its height, which the client rejects, is copied with
 * transparent rows below it.
 */
public final class CanvasPackGenerator {

    public static final int MAX_CELL_SIZE = 256;

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final TextFonts textFonts;

    public CanvasPackGenerator(@NotNull TextFonts textFonts) {
        this.textFonts = textFonts;
    }

    /**
     * Generates the files.
     *
     * @param request What the canvases need.
     * @param lock    The characters given to cells.
     * @return The files, by path inside the pack, and what they contain.
     */
    public @NotNull Result generate(@NotNull CanvasRequest request, @NotNull CodepointLock lock) {
        SortedMap<String, byte[]> files = new TreeMap<>();
        Map<GlyphVariant, String> glyphs = new HashMap<>();
        List<String> texts = new ArrayList<>();

        if (request.glyphs().isEmpty() && request.texts().isEmpty()) {
            return new Result(files, CanvasLayout.EMPTY);
        }

        this.writeFont(files, SpaceFont.KEY, List.of(SpaceFont.provider()));

        Map<Integer, List<JsonObject>> canvasFonts = new TreeMap<>();
        Map<Key, Optional<BufferedImage>> images = new HashMap<>();
        for (GlyphVariant variant : request.glyphs()) {
            GlyphCell cell = variant.cell();
            if (cell.cellWidth() > MAX_CELL_SIZE || cell.cellHeight() > MAX_CELL_SIZE || cell.cellWidth() <= 0 || cell.cellHeight() <= 0) {
                Logger.info("The texture " + cell.texture().asString() + " has a cell of " + cell.cellWidth() + "x" + cell.cellHeight() + ", the client only draws cells up to 256x256", Logger.LogType.WARNING);
                continue;
            }
            if (variant.y() < 0) continue;

            GlyphProvider owner = request.owner(cell.texture());
            Optional<BufferedImage> image = images.computeIfAbsent(cell.texture(), texture -> owner == null ? Optional.empty() : owner.readTexture(texture));
            int ascent = 7 - variant.y();
            int codepoint = lock.codepoint(variant.lockKey());
            String character = Character.toString(codepoint);

            JsonObject provider;
            if (ascent > cell.height()) {
                if (image.isEmpty()) {
                    Logger.info("The texture " + cell.texture().asString() + " cannot be read, so it cannot be drawn at y " + variant.y(), Logger.LogType.WARNING);
                    continue;
                }
                provider = this.paddedProvider(files, variant, image.get(), ascent, character);
            } else {
                if (cell.texture().namespace().equals("zmenu")) {
                    if (image.isEmpty()) {
                        Logger.info("The texture " + cell.texture().asString() + " cannot be read", Logger.LogType.WARNING);
                        continue;
                    }
                    files.putIfAbsent("assets/zmenu/textures/" + cell.texture().value(), TextureMetrics.png(image.get()));
                }
                provider = bitmap(cell.texture(), cell.height(), ascent, grid(cell, character));
            }

            canvasFonts.computeIfAbsent(variant.y(), y -> new ArrayList<>()).add(provider);
            glyphs.put(variant, character);
        }

        canvasFonts.forEach((y, providers) -> {
            List<JsonObject> all = new ArrayList<>();
            all.add(SpaceFont.provider());
            all.addAll(providers);
            this.writeFont(files, CanvasLayout.canvasFont(y), all);
        });

        for (String text : request.texts()) {
            int separator = text.lastIndexOf('@');
            String name = text.substring(0, separator);
            int y = Integer.parseInt(text.substring(separator + 1));
            CanvasTextFont font = this.textFonts.get(name);
            if (font == null || !font.isReady() || y < 0) continue;
            files.putAll(font.files());
            this.writeFont(files, CanvasTextFont.fontKey(name, y), font.providers(y).asList().stream().map(JsonElement::getAsJsonObject).toList());
            texts.add(text);
        }

        return new Result(files, new CanvasLayout(glyphs, Set.copyOf(texts)));
    }

    private @NotNull JsonObject paddedProvider(@NotNull SortedMap<String, byte[]> files, @NotNull GlyphVariant variant, @NotNull BufferedImage image, int ascent, @NotNull String character) {
        GlyphCell cell = variant.cell();
        double scale = (double) cell.height() / cell.cellHeight();
        int paddedCellHeight = Math.max(cell.cellHeight(), (int) Math.ceil(ascent / scale));
        int paddedHeight = Math.max(ascent, (int) Math.round(paddedCellHeight * scale));

        BufferedImage padded = TextureMetrics.copy(image, cell.column() * cell.cellWidth(), cell.row() * cell.cellHeight(), cell.cellWidth(), paddedCellHeight);
        byte[] bytes = TextureMetrics.png(padded);
        String path = "font/canvas/padded/" + PackOutput.contentHash(new TreeMap<>(Map.of(variant.lockKey() + "@" + paddedCellHeight, bytes))).substring(0, 16) + ".png";
        files.put("assets/zmenu/textures/" + path, bytes);

        JsonArray chars = new JsonArray();
        chars.add(character);
        return bitmap(Key.key("zmenu", path), paddedHeight, ascent, chars);
    }

    private static @NotNull JsonArray grid(@NotNull GlyphCell cell, @NotNull String character) {
        JsonArray rows = new JsonArray();
        for (int row = 0; row < cell.gridRows(); row++) {
            StringBuilder builder = new StringBuilder();
            for (int column = 0; column < cell.gridColumns(); column++) {
                builder.append(row == cell.row() && column == cell.column() ? character : "\u0000");
            }
            rows.add(builder.toString());
        }
        return rows;
    }

    private static @NotNull JsonObject bitmap(@NotNull Key file, int height, int ascent, @NotNull JsonArray chars) {
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "bitmap");
        provider.addProperty("file", file.asString());
        provider.addProperty("height", height);
        provider.addProperty("ascent", ascent);
        provider.add("chars", chars);
        return provider;
    }

    private void writeFont(@NotNull SortedMap<String, byte[]> files, @NotNull Key font, @NotNull List<JsonObject> providers) {
        JsonArray array = new JsonArray();
        providers.forEach(array::add);
        JsonObject root = new JsonObject();
        root.add("providers", array);
        files.put("assets/" + font.namespace() + "/font/" + font.value() + ".json", GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * The generated files and what they contain.
     *
     * @param files  The files, by path inside the pack.
     * @param layout The characters and fonts canvases are drawn with.
     */
    public record Result(@NotNull SortedMap<String, byte[]> files, @NotNull CanvasLayout layout) {
    }
}
