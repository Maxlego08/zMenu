package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A TrueType font of {@code resource-pack/fonts.yml}, measured with the same file the client draws.
 *
 * <pre>
 * title:
 *   type: ttf
 *   file: fonts/title.ttf
 *   size: 12
 *   oversample: 4
 *   shift: 0
 * </pre>
 * {@code shift} moves every copy of the font down, to line the top of its letters up with the
 * {@code y} of the elements using it.
 */
public final class TrueTypeTextFont implements CanvasTextFont {

    private static final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);

    private final String name;
    private final byte[] file;
    private final Font font;
    private final float size;
    private final float oversample;
    private final float shift;
    private final int lineHeight;
    private final Map<Integer, Double> advances = new ConcurrentHashMap<>();

    public TrueTypeTextFont(@NotNull String name, byte @NotNull [] file, @NotNull Font font, float size, float oversample, float shift, int lineHeight) {
        this.name = name;
        this.file = file;
        this.font = font.deriveFont(size);
        this.size = size;
        this.oversample = oversample;
        this.shift = shift;
        this.lineHeight = lineHeight;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public double advance(int codepoint) {
        return this.advances.computeIfAbsent(codepoint, character -> {
            GlyphVector vector = this.font.createGlyphVector(RENDER_CONTEXT, Character.toString(character));
            double advance = vector.getGlyphMetrics(0).getAdvance();
            return Math.rint(advance * 4) / 4;
        });
    }

    @Override
    public int lineHeight() {
        return this.lineHeight;
    }

    @Override
    public @NotNull JsonArray providers(int y) {
        JsonArray shiftValue = new JsonArray();
        shiftValue.add(0);
        shiftValue.add(this.shift + y);

        JsonObject provider = new JsonObject();
        provider.addProperty("type", "ttf");
        provider.addProperty("file", "zmenu:" + this.name + ".ttf");
        provider.add("shift", shiftValue);
        provider.addProperty("size", this.size);
        provider.addProperty("oversample", this.oversample);

        JsonArray providers = new JsonArray();
        providers.add(provider);
        return providers;
    }

    @Override
    public @NotNull SortedMap<String, byte[]> files() {
        SortedMap<String, byte[]> files = new TreeMap<>();
        files.put("assets/zmenu/font/" + this.name + ".ttf", this.file);
        return files;
    }
}
