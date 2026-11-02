package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A key that chooses something the resource pack must hold, such as an image or a font, and may hold
 * placeholders. Since the pack is generated before any player opens the dialog, a placeholder must come with
 * the list of values it can give, and each of them is generated:
 *
 * <pre>
 * glyph: "ranks/%luckperms_primary_group_name%"
 * glyph-values: [default, vip, mvp]   # or the full values: [ranks/default, ranks/vip, ranks/mvp]
 * glyph-default: default              # optional, the first value otherwise
 * </pre>
 * With exactly one placeholder, a listed value is what the placeholder gives, and is put in the template;
 * a value already written in full is kept as it is. With several placeholders, values are written in full. A
 * result outside the list draws the default, with one warning per result.
 *
 * @param raw          The value as written.
 * @param values       Every value the pack holds, in full, without duplicates.
 * @param fallback     The value drawn when a result is not in the list.
 * @param written      The values as written, to write them back.
 * @param writtenDefault The default as written, or null when it was not written.
 * @param normalize    Checks and normalizes a full value.
 */
public record PackValue(@NotNull String raw, @NotNull List<String> values, @NotNull String fallback, @NotNull List<String> written,
                        @Nullable String writtenDefault, @NotNull UnaryOperator<String> normalize) {

    private static final Pattern PLACEHOLDER = Pattern.compile("%[^%\\s]+%");
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    public PackValue {
        values = List.copyOf(values);
        written = List.copyOf(written);
    }

    /**
     * @param value A value without placeholders.
     * @return The value.
     */
    public static @NotNull PackValue of(@NotNull String value) {
        return new PackValue(value, List.of(value), value, List.of(), null, UnaryOperator.identity());
    }

    /**
     * Reads a pack key, with its {@code <key>-values} and {@code <key>-default}.
     *
     * @param section   The section of the element.
     * @param key       The key.
     * @param fallback  The value when the key is missing, or null when the key is required.
     * @param normalize Checks a full value and gives it normalized; throws an {@link IllegalArgumentException}
     *                  with the reason when it is not valid.
     * @param name      The name of the element, for error messages.
     * @param source    Where it is read from, for error messages.
     * @return The value.
     */
    public static @NotNull PackValue read(@NotNull ConfigurationSection section, @NotNull String key, @Nullable String fallback,
                                          @NotNull UnaryOperator<String> normalize, @NotNull String name, @NotNull String source) {
        String valuesKey = key + "-values";
        String defaultKey = key + "-default";
        String raw = section.contains(key) ? section.getString(key) : fallback;
        if (raw == null || raw.isBlank()) throw ElementSections.error(name, source, "needs " + key);

        if (!isPlaceholder(raw)) {
            if (section.contains(valuesKey) || section.contains(defaultKey)) {
                throw ElementSections.error(name, source, "has " + valuesKey + " or " + defaultKey + " but its " + key + " holds no placeholder");
            }
            String value = check(normalize, raw, key, name, source);
            return new PackValue(raw, List.of(value), value, List.of(), null, normalize);
        }

        List<String> written = section.getStringList(valuesKey);
        if (written.isEmpty()) {
            throw ElementSections.error(name, source, "uses a placeholder in " + key + ", so it needs " + valuesKey
                    + ": the list of values it can give, which are added to the resource pack");
        }
        Set<String> values = new LinkedHashSet<>();
        for (String value : written) values.add(check(normalize, expand(raw, value), key, name, source));

        String writtenDefault = section.getString(defaultKey);
        String defaultValue = writtenDefault == null ? values.iterator().next() : check(normalize, expand(raw, writtenDefault), key, name, source);
        if (!values.contains(defaultValue)) {
            throw ElementSections.error(name, source, "has " + defaultKey + " " + writtenDefault + ", which is not in " + valuesKey);
        }
        return new PackValue(raw, new ArrayList<>(values), defaultValue, written, writtenDefault, normalize);
    }

    private static @NotNull String check(@NotNull UnaryOperator<String> normalize, @NotNull String value, @NotNull String key, @NotNull String name,
                                         @NotNull String source) {
        try {
            return normalize.apply(value);
        } catch (IllegalArgumentException exception) {
            throw ElementSections.error(name, source, "has the " + key + " " + value + ", which " + exception.getMessage());
        }
    }

    /**
     * Puts a listed value in a template with exactly one placeholder; a value already written in full, or a
     * template with several placeholders, gives the value as it is.
     *
     * @param raw   The template.
     * @param value A listed value.
     * @return The full value.
     */
    static @NotNull String expand(@NotNull String raw, @NotNull String value) {
        Matcher matcher = PLACEHOLDER.matcher(raw);
        if (!matcher.find()) return value;
        int start = matcher.start();
        int end = matcher.end();
        if (matcher.find()) return value;
        String prefix = raw.substring(0, start);
        String suffix = raw.substring(end);
        boolean full = !(prefix.isEmpty() && suffix.isEmpty()) && value.length() >= prefix.length() + suffix.length()
                && value.startsWith(prefix) && value.endsWith(suffix);
        return full ? value : prefix + value + suffix;
    }

    private static boolean isPlaceholder(@NotNull String raw) {
        return PLACEHOLDER.matcher(raw).find();
    }

    /**
     * @return True if the value holds placeholders.
     */
    public boolean isPlaceholder() {
        return isPlaceholder(this.raw);
    }

    /**
     * Resolves the value for a player.
     *
     * @param context The player and the placeholders.
     * @return One of the values, the default when the result is not in the list.
     */
    public @NotNull String resolve(@NotNull CanvasRenderContext context) {
        if (!this.isPlaceholder()) return this.fallback;
        String parsed = context.resolve(this.raw).trim();
        String value;
        try {
            value = this.normalize.apply(parsed);
        } catch (IllegalArgumentException exception) {
            value = parsed;
        }
        if (this.values.contains(value)) return value;
        if (REPORTED.add(this.raw + "=" + parsed)) {
            Logger.info("The canvas value " + this.raw + " gave " + parsed + ", which is not in its list of values: " + this.fallback + " is drawn instead",
                    Logger.LogType.WARNING);
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
        section.set(key, this.raw);
        if (!this.written.isEmpty()) section.set(key + "-values", this.written);
        if (this.writtenDefault != null) section.set(key + "-default", this.writtenDefault);
    }

    @Override
    public String toString() {
        return this.raw;
    }
}
