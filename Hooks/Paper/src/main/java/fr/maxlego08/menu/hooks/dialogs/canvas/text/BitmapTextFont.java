package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * A bitmap font of {@code resource-pack/fonts.yml}: one texture holding a grid of characters.
 *
 * <pre>
 * pixel:
 *   type: bitmap
 *   file: fonts/pixel.png
 *   height: 8
 *   ascent: 7
 *   space-width: 4
 *   chars:
 *     - "ABCDEFGHIJKLMNOP"
 *     - "QRSTUVWXYZ012345"
 * </pre>
 */
public final class BitmapTextFont implements CanvasTextFont {

    private final String name;
    private final byte[] texture;
    private final JsonObject provider;
    private final int spaceWidth;
    private final int lineHeight;
    private final Map<Integer, Double> advances = new HashMap<>();

    public BitmapTextFont(@NotNull String name, byte @NotNull [] texture, @NotNull BufferedImage image, @NotNull List<String> chars, int height, int ascent, int spaceWidth, int lineHeight) {
        this.name = name;
        this.texture = texture;
        this.spaceWidth = spaceWidth;
        this.lineHeight = lineHeight;

        JsonArray rows = new JsonArray();
        chars.forEach(rows::add);
        this.provider = new JsonObject();
        this.provider.addProperty("type", "bitmap");
        this.provider.addProperty("file", "zmenu:" + this.texturePath());
        this.provider.addProperty("height", height);
        this.provider.addProperty("ascent", ascent);
        this.provider.add("chars", rows);

        this.advances.put((int) ' ', (double) spaceWidth);
        BitmapAdvances.measure(this.provider, image, this.advances);
    }

    private @NotNull String texturePath() {
        return "font/text/" + this.name + ".png";
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
        return this.advances.getOrDefault(codepoint, (double) this.spaceWidth);
    }

    @Override
    public int lineHeight() {
        return this.lineHeight;
    }

    @Override
    public @NotNull JsonArray providers(int y) {
        JsonObject space = new JsonObject();
        space.addProperty("type", "space");
        JsonObject spaceAdvances = new JsonObject();
        spaceAdvances.addProperty(" ", this.spaceWidth);
        space.add("advances", spaceAdvances);

        JsonArray providers = new JsonArray();
        providers.add(space);
        providers.add(BitmapAdvances.shifted(this.provider, y));
        return providers;
    }

    @Override
    public @NotNull SortedMap<String, byte[]> files() {
        SortedMap<String, byte[]> files = new TreeMap<>();
        files.put("assets/zmenu/textures/" + this.texturePath(), this.texture);
        return files;
    }
}
