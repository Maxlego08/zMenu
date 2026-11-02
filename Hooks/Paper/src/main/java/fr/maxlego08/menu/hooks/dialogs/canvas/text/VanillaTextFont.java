package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.hooks.dialogs.canvas.vanilla.VanillaAssets;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The vanilla font, moved down by copying its bitmap providers with another ascent. The copies point at
 * the textures of the client, so the pack holds no vanilla texture.
 * <p>
 * Characters only found in Unifont cannot be moved: they are drawn by a reference to the vanilla Unifont
 * at their usual position.
 */
public final class VanillaTextFont implements CanvasTextFont {

    public static final String NAME = "default";

    private static final Key INCLUDE_DEFAULT = Key.key("include/default");
    private static final Key INCLUDE_SPACE = Key.key("include/space");
    private static final Key INCLUDE_UNIFONT = Key.key("include/unifont");
    private static final double UNKNOWN_ADVANCE = 6;

    private final VanillaAssets assets;
    private volatile List<JsonObject> bitmapProviders;
    private volatile Map<Integer, Double> advances;

    public VanillaTextFont(@NotNull VanillaAssets assets) {
        this.assets = assets;
    }

    @Override
    public @NotNull String name() {
        return NAME;
    }

    @Override
    public boolean isReady() {
        return this.load();
    }

    /**
     * Forgets the measured characters, to measure them again from the current files.
     */
    public synchronized void reload() {
        this.bitmapProviders = null;
        this.advances = null;
    }

    private synchronized boolean load() {
        if (this.advances != null) return true;
        if (!this.assets.isReady()) return false;

        List<JsonObject> bitmaps = this.assets.providers(INCLUDE_DEFAULT).stream().filter(provider -> "bitmap".equals(type(provider))).toList();
        if (bitmaps.isEmpty()) return false;

        Map<Integer, Double> measured = new HashMap<>();
        for (JsonObject provider : this.assets.providers(INCLUDE_SPACE)) {
            if ("space".equals(type(provider))) BitmapAdvances.measureSpaces(provider, measured);
        }
        for (JsonObject provider : bitmaps) {
            Optional<BufferedImage> image = this.assets.texture(Key.key(provider.get("file").getAsString()));
            image.ifPresent(texture -> BitmapAdvances.measure(provider, texture, measured));
        }
        this.bitmapProviders = bitmaps;
        this.advances = measured;
        return true;
    }

    private static String type(@NotNull JsonObject provider) {
        return provider.has("type") ? provider.get("type").getAsString() : "";
    }

    @Override
    public double advance(int codepoint) {
        if (!this.load()) return UNKNOWN_ADVANCE;
        return this.advances.getOrDefault(codepoint, UNKNOWN_ADVANCE);
    }

    @Override
    public int lineHeight() {
        return 9;
    }

    @Override
    public @NotNull JsonArray providers(int y) {
        JsonArray providers = new JsonArray();
        if (!this.load()) return providers;
        providers.add(reference(INCLUDE_SPACE));
        for (JsonObject provider : this.bitmapProviders) providers.add(BitmapAdvances.shifted(provider, y));
        providers.add(reference(INCLUDE_UNIFONT));
        return providers;
    }

    private static @NotNull JsonObject reference(@NotNull Key font) {
        JsonObject reference = new JsonObject();
        reference.addProperty("type", "reference");
        reference.addProperty("id", font.asString());
        return reference;
    }

    @Override
    public @NotNull SortedMap<String, byte[]> files() {
        return new TreeMap<>();
    }
}
