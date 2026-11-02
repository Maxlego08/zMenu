package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The fonts canvas text can use: the vanilla font, called {@code default}, and the fonts of
 * {@code resource-pack/fonts.yml}.
 */
public final class TextFonts {

    private static final Pattern NAME = Pattern.compile("[a-z0-9_.-]+");
    private static final Set<String> BITMAP_KEYS = Set.of("type", "file", "height", "ascent", "chars", "space-width", "line-height");
    private static final Set<String> TTF_KEYS = Set.of("type", "file", "size", "oversample", "shift", "line-height");

    private final VanillaTextFont vanilla;
    private volatile Map<String, CanvasTextFont> fonts = Map.of();

    public TextFonts(@NotNull VanillaTextFont vanilla) {
        this.vanilla = vanilla;
    }

    /**
     * Reads {@code fonts.yml} again. A font that cannot be read is logged and left out.
     *
     * @param resourcePackFolder The {@code resource-pack} folder.
     */
    public void load(@NotNull Path resourcePackFolder) {
        this.vanilla.reload();
        Map<String, CanvasTextFont> loaded = new LinkedHashMap<>();
        loaded.put(VanillaTextFont.NAME, this.vanilla);

        Path file = resourcePackFolder.resolve("fonts.yml");
        if (Files.isRegularFile(file)) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file.toFile());
            for (String name : configuration.getKeys(false)) {
                ConfigurationSection section = configuration.getConfigurationSection(name);
                if (section == null) continue;
                try {
                    loaded.put(name, this.read(resourcePackFolder, name, section));
                } catch (IllegalArgumentException | IOException exception) {
                    Logger.info("The font " + name + " of " + file + " cannot be used: " + exception.getMessage(), Logger.LogType.WARNING);
                }
            }
        }
        this.fonts = Map.copyOf(loaded);
    }

    private @NotNull CanvasTextFont read(@NotNull Path folder, @NotNull String name, @NotNull ConfigurationSection section) throws IOException {
        if (!NAME.matcher(name).matches()) throw new IllegalArgumentException("a font name may only hold a-z, 0-9, _, . and -");
        if (VanillaTextFont.NAME.equals(name)) throw new IllegalArgumentException("the name default belongs to the vanilla font");

        String type = section.getString("type", "");
        String path = section.getString("file");
        if (path == null || path.isBlank()) throw new IllegalArgumentException("file is missing");
        Path source = folder.resolve(path).normalize();
        if (!source.startsWith(folder)) throw new IllegalArgumentException("file must be inside the resource-pack folder");
        if (!Files.isRegularFile(source)) throw new IllegalArgumentException("the file " + source + " does not exist");
        byte[] bytes = Files.readAllBytes(source);

        switch (type) {
            case "bitmap" -> {
                rejectUnknownKeys(section, BITMAP_KEYS);
                BufferedImage image = TextureMetrics.read(bytes);
                if (image == null) throw new IllegalArgumentException("the file is not a PNG");
                List<String> chars = section.getStringList("chars");
                if (chars.isEmpty()) throw new IllegalArgumentException("chars is missing");
                int height = section.getInt("height", 8);
                int ascent = section.getInt("ascent", 7);
                if (ascent > height) throw new IllegalArgumentException("ascent cannot be higher than height");
                return new BitmapTextFont(name, bytes, image, chars, height, ascent, section.getInt("space-width", 4), section.getInt("line-height", height + 1));
            }
            case "ttf" -> {
                rejectUnknownKeys(section, TTF_KEYS);
                Font font;
                try {
                    font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(bytes));
                } catch (FontFormatException exception) {
                    throw new IllegalArgumentException("the file is not a TrueType font");
                }
                float size = (float) section.getDouble("size", 11);
                return new TrueTypeTextFont(name, bytes, font, size, (float) section.getDouble("oversample", 4), (float) section.getDouble("shift", 0),
                        section.getInt("line-height", Math.round(size) + 1));
            }
            default -> throw new IllegalArgumentException("type must be bitmap or ttf");
        }
    }

    private static void rejectUnknownKeys(@NotNull ConfigurationSection section, @NotNull Set<String> allowed) {
        for (String key : section.getKeys(false)) {
            if (!allowed.contains(key)) throw new IllegalArgumentException("unknown key " + key + ", allowed: " + String.join(", ", allowed));
        }
    }

    /**
     * @param name The name of a font.
     * @return The font, or null if it does not exist.
     */
    public @Nullable CanvasTextFont get(@NotNull String name) {
        return this.fonts.get(name);
    }

    /**
     * @return Every font.
     */
    public @NotNull Collection<CanvasTextFont> all() {
        return this.fonts.values();
    }
}
