package fr.maxlego08.menu.api.dialog.canvas;

import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A value of a canvas key that may hold placeholders: zMenu placeholders, then PlaceholderAPI, are resolved
 * for each player when the canvas is drawn, and the result is read as the key's type.
 * <p>
 * Only keys that do not change the resource pack use it freely: text, widths and alignments of text boxes,
 * heads, sprites, the size of the canvas. A result that cannot be read gives the key's default, with one
 * warning per result.
 *
 * @param <T> The type of the key.
 */
public final class CanvasValue<T> {

    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    private final String raw;
    private final T fixed;
    private final T fallback;
    private final Function<String, T> parser;

    private CanvasValue(@NotNull String raw, @Nullable T fixed, @NotNull T fallback, @NotNull Function<String, T> parser) {
        this.raw = raw;
        this.fixed = fixed;
        this.fallback = fallback;
        this.parser = parser;
    }

    /**
     * @param value A value without placeholders.
     * @return The value.
     */
    public static <T> @NotNull CanvasValue<T> of(@NotNull T value) {
        return new CanvasValue<>(String.valueOf(value), value, value, text -> value);
    }

    private static <T> @NotNull CanvasValue<T> read(@NotNull ConfigurationSection section, @NotNull String key, @NotNull T fallback,
                                                    @NotNull Function<String, T> parser, @NotNull String expected, @NotNull String where) {
        if (!section.contains(key)) return new CanvasValue<>(String.valueOf(fallback), fallback, fallback, parser);
        String raw = String.valueOf(section.get(key)).trim();
        if (raw.contains("%")) return new CanvasValue<>(raw, null, fallback, parser);
        T value = parser.apply(raw);
        if (value == null) throw new DialogException(where + " needs " + expected + " for " + key + ", or a placeholder, not " + raw);
        return new CanvasValue<>(raw, value, fallback, parser);
    }

    /**
     * Reads a whole number.
     *
     * @param section  The section.
     * @param key      The key.
     * @param fallback The value when the key is missing or its placeholder gives something else.
     * @param min      The smallest value allowed.
     * @param max      The largest value allowed.
     * @param where    Which element is read, for error messages.
     * @return The value.
     */
    public static @NotNull CanvasValue<Integer> number(@NotNull ConfigurationSection section, @NotNull String key, int fallback, int min, int max, @NotNull String where) {
        return read(section, key, fallback, text -> {
            try {
                long value = Math.round(Double.parseDouble(text));
                return value < min || value > max ? null : (int) value;
            } catch (NumberFormatException exception) {
                return null;
            }
        }, "a whole number from " + min + " to " + max, where);
    }

    /**
     * Reads a text, which any result is.
     */
    public static @NotNull CanvasValue<String> text(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String fallback, @NotNull String where) {
        return read(section, key, fallback, text -> text, "a text", where);
    }

    /**
     * Reads {@code true}/{@code false} (also {@code yes}/{@code no} and {@code 1}/{@code 0}).
     */
    public static @NotNull CanvasValue<Boolean> bool(@NotNull ConfigurationSection section, @NotNull String key, boolean fallback, @NotNull String where) {
        return read(section, key, fallback, text -> switch (text.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1" -> Boolean.TRUE;
            case "false", "no", "0" -> Boolean.FALSE;
            default -> null;
        }, "true or false", where);
    }

    /**
     * Reads the name of a constant of an enum, in any case, with {@code -} read as {@code _}.
     */
    public static <E extends Enum<E>> @NotNull CanvasValue<E> enumeration(@NotNull ConfigurationSection section, @NotNull String key, @NotNull Class<E> type,
                                                                           @NotNull E fallback, @NotNull String where) {
        return read(section, key, fallback, text -> {
            try {
                return Enum.valueOf(type, text.toUpperCase(Locale.ROOT).replace('-', '_'));
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }, "one of " + String.join(", ", Arrays.stream(type.getEnumConstants()).map(constant -> constant.name().toLowerCase(Locale.ROOT)).toList()), where);
    }

    /**
     * Reads a key such as {@code minecraft:blocks}; a value without namespace is in {@code minecraft}.
     */
    public static @NotNull CanvasValue<Key> key(@NotNull ConfigurationSection section, @NotNull String key, @NotNull Key fallback, @NotNull String where) {
        return read(section, key, fallback, text -> {
            String value = text.toLowerCase(Locale.ROOT);
            int separator = value.indexOf(':');
            String namespace = separator < 0 ? Key.MINECRAFT_NAMESPACE : value.substring(0, separator);
            String path = separator < 0 ? value : value.substring(separator + 1);
            return Key.parseableNamespace(namespace) && Key.parseableValue(path) ? Key.key(namespace, path) : null;
        }, "a key such as minecraft:blocks", where);
    }

    /**
     * @return True if the value holds placeholders.
     */
    public boolean isPlaceholder() {
        return this.fixed == null;
    }

    /**
     * @return The value when it holds no placeholder, or null.
     */
    public @Nullable T fixed() {
        return this.fixed;
    }

    /**
     * @return The value used when a placeholder gives something that cannot be read.
     */
    public @NotNull T fallback() {
        return this.fallback;
    }

    /**
     * @return The value when it holds no placeholder, else its default.
     */
    public @NotNull T fixedOrFallback() {
        return this.fixed == null ? this.fallback : this.fixed;
    }

    /**
     * @return The value as written.
     */
    public @NotNull String raw() {
        return this.raw;
    }

    /**
     * Resolves the value for a player.
     *
     * @param context The player and the placeholders.
     * @return The value, or its default when the result cannot be read.
     */
    public @NotNull T resolve(@NotNull CanvasRenderContext context) {
        if (this.fixed != null) return this.fixed;
        String parsed = context.resolve(this.raw).trim();
        T value = this.parser.apply(parsed);
        if (value != null) return value;
        if (REPORTED.add(this.raw + "=" + parsed)) {
            Logger.info("The canvas value " + this.raw + " gave " + parsed + ", which cannot be used: " + this.fallback + " is used instead", Logger.LogType.WARNING);
        }
        return this.fallback;
    }

    /**
     * Writes the value back as it was written.
     *
     * @param section The section.
     * @param key     The key.
     */
    public void serialize(@NotNull ConfigurationSection section, @NotNull String key) {
        if (this.fixed == null) {
            section.set(key, this.raw);
        } else if (this.fixed instanceof Number || this.fixed instanceof Boolean) {
            section.set(key, this.fixed);
        } else if (this.fixed instanceof Enum<?> constant) {
            section.set(key, constant.name().toLowerCase(Locale.ROOT).replace('_', '-'));
        } else if (this.fixed instanceof Key value) {
            section.set(key, value.asString());
        } else {
            section.set(key, this.raw);
        }
    }

    @Override
    public String toString() {
        return this.raw;
    }
}
