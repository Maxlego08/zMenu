package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.exceptions.DialogException;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Checks shared by the canvas element loaders.
 */
public final class ElementSections {

    private static final Pattern TEXTURE = Pattern.compile("[a-z0-9_./-]+");

    private ElementSections() {
    }

    public static void rejectUnknownKeys(@NotNull ConfigurationSection section, @NotNull Set<String> allowed, @NotNull String name, @NotNull String source) {
        for (String key : section.getKeys(false)) {
            if (!allowed.contains(key)) {
                throw new DialogException("The canvas element " + name + " of " + source + " has the unknown key " + key + ", allowed: " + String.join(", ", allowed));
            }
        }
    }

    public static int requireInt(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        if (!section.isInt(key)) throw new DialogException("The canvas element " + name + " of " + source + " needs a whole number " + key);
        return section.getInt(key);
    }

    public static @NotNull String requireString(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        String value = section.getString(key);
        if (value == null || value.isBlank()) throw new DialogException("The canvas element " + name + " of " + source + " needs " + key);
        return value;
    }

    /**
     * Checks a texture of {@code resource-pack/textures}, written with or without {@code zmenu:}.
     *
     * @param value The texture.
     * @return The texture without {@code zmenu:}.
     * @throws IllegalArgumentException If it is not a texture path.
     */
    public static @NotNull String texture(@NotNull String value) {
        String texture = value.startsWith("zmenu:") ? value.substring("zmenu:".length()) : value;
        if (!TEXTURE.matcher(texture).matches() || texture.contains("..")) {
            throw new IllegalArgumentException("must be a texture of resource-pack/textures, in lowercase letters, digits, _, -, . and /");
        }
        return texture;
    }

    public static @NotNull DialogException error(@NotNull String name, @NotNull String source, @NotNull String message) {
        return new DialogException("The canvas element " + name + " of " + source + " " + message);
    }
}
