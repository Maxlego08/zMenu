package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A coordinate of a canvas element: a number, or a placeholder resolved when the canvas is drawn.
 * <p>
 * Any x works, since the x of an element only moves the cursor. A y needs the list of values it can take,
 * because each one is a font of the resource pack: a placeholder y is written with its {@code y-values},
 * and a value outside them is moved to the nearest one.
 *
 * <pre>
 * y: "%fishing_fish_y%"
 * y-values: 0-43        # a range, or a list: [0, 10, 20]
 * </pre>
 *
 * @param raw    The value as written.
 * @param fixed  The value when it is a number, or null for a placeholder.
 * @param values The values a placeholder y can take, sorted, empty for an x.
 */
public record PositionValue(@NotNull String raw, @Nullable Integer fixed, @NotNull List<Integer> values) {

    private static final int MAX_VALUES = 256;
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    public PositionValue {
        values = List.copyOf(values);
    }

    /**
     * @return True if the coordinate is a placeholder.
     */
    public boolean isPlaceholder() {
        return this.fixed == null;
    }

    /**
     * @param value A number.
     * @return The coordinate.
     */
    public static @NotNull PositionValue of(int value) {
        return new PositionValue(String.valueOf(value), value, List.of(value));
    }

    /**
     * Reads an x, which may be a placeholder without listing its values.
     */
    static @NotNull PositionValue readX(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        if (section.isInt(key)) return of(section.getInt(key));
        String raw = section.getString(key);
        if (raw == null || raw.isBlank()) throw ElementSections.error(name, source, "needs " + key);
        if (!raw.contains("%")) throw ElementSections.error(name, source, "needs a whole number or a placeholder for " + key);
        return new PositionValue(raw, null, List.of());
    }

    /**
     * Reads a y: a number from 0, or a placeholder with the values it can take under {@code <key>-values}.
     */
    static @NotNull PositionValue readY(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        if (section.isInt(key)) {
            int value = section.getInt(key);
            if (value < 0) throw ElementSections.error(name, source, "cannot have a negative " + key);
            return of(value);
        }
        String raw = section.getString(key);
        if (raw == null || raw.isBlank()) throw ElementSections.error(name, source, "needs " + key);
        if (!raw.contains("%")) throw ElementSections.error(name, source, "needs a whole number or a placeholder for " + key);

        String valuesKey = key + "-values";
        TreeSet<Integer> values = new TreeSet<>();
        if (section.isList(valuesKey)) {
            for (Object value : section.getList(valuesKey, List.of())) {
                try {
                    values.add(Integer.parseInt(String.valueOf(value).trim()));
                } catch (NumberFormatException exception) {
                    throw ElementSections.error(name, source, "has a value that is not a whole number in " + valuesKey + ": " + value);
                }
            }
        } else {
            String range = section.getString(valuesKey);
            if (range == null) throw ElementSections.error(name, source, "uses a placeholder for " + key + " and needs " + valuesKey + ", such as 0-40 or [0, 10, 20]");
            String[] bounds = range.split("-");
            try {
                int from = Integer.parseInt(bounds[0].trim());
                int to = bounds.length > 1 ? Integer.parseInt(bounds[1].trim()) : from;
                for (int value = Math.min(from, to); value <= Math.max(from, to); value++) values.add(value);
            } catch (NumberFormatException exception) {
                throw ElementSections.error(name, source, "has an invalid " + valuesKey + ": " + range);
            }
        }
        if (values.isEmpty()) throw ElementSections.error(name, source, "needs at least one value in " + valuesKey);
        if (values.first() < 0) throw ElementSections.error(name, source, "cannot have a negative value in " + valuesKey);
        if (values.size() > MAX_VALUES) throw ElementSections.error(name, source, "has more than " + MAX_VALUES + " values in " + valuesKey);
        return new PositionValue(raw, null, new ArrayList<>(values));
    }

    /**
     * @return Every value this coordinate can take, for a y.
     */
    public @NotNull List<Integer> all() {
        return this.values;
    }

    /**
     * @param offset A distance to add to every value.
     * @return Every value this coordinate can take, moved by {@code offset}.
     */
    public @NotNull List<Integer> all(int offset) {
        return this.values.stream().map(value -> value + offset).toList();
    }

    /**
     * Resolves the coordinate for a player.
     *
     * @param context The player and the placeholders.
     * @return The value, moved to the nearest allowed value for a y.
     */
    public int resolve(@NotNull CanvasRenderContext context) {
        if (this.fixed != null) return this.fixed;
        String parsed = context.resolve(this.raw).trim();
        int value;
        try {
            value = (int) Math.round(Double.parseDouble(parsed));
        } catch (NumberFormatException exception) {
            if (REPORTED.add(this.raw + "=" + parsed)) {
                Logger.info("The canvas position " + this.raw + " gave " + parsed + ", which is not a number", Logger.LogType.WARNING);
            }
            return this.values.isEmpty() ? 0 : this.values.getFirst();
        }
        return this.values.isEmpty() ? value : this.nearest(value);
    }

    private int nearest(int value) {
        int best = this.values.getFirst();
        for (int candidate : this.values) {
            if (Math.abs(candidate - value) < Math.abs(best - value)) best = candidate;
        }
        if (best != value && REPORTED.add(this.raw + "#" + value)) {
            Logger.info("The canvas position " + this.raw + " gave " + value + ", which is not in its values: drawn at " + best, Logger.LogType.WARNING);
        }
        return best;
    }

    /**
     * Writes the coordinate back.
     *
     * @param section The section of the element.
     * @param key     The key of the coordinate.
     */
    void serialize(@NotNull ConfigurationSection section, @NotNull String key) {
        if (this.fixed != null) {
            section.set(key, this.fixed);
            return;
        }
        section.set(key, this.raw);
        if (this.values.isEmpty()) return;
        int first = this.values.getFirst();
        int last = this.values.getLast();
        boolean contiguous = this.values.size() == last - first + 1;
        section.set(key + "-values", contiguous ? first + "-" + last : this.values);
    }
}
