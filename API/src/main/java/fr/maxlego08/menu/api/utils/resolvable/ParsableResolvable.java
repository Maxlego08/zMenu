package fr.maxlego08.menu.api.utils.resolvable;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ParsableResolvable<T> implements Resolvable<T> {
    private final @Nullable T resolvedValue;
    private final @Nullable String expression;

    protected ParsableResolvable(@Nullable T resolvedValue, @Nullable String expression) {
        if ((resolvedValue == null) == (expression == null)) {
            throw new IllegalArgumentException(
                    "Exactly one of resolvedValue or expression must be non-null"
            );
        }

        this.resolvedValue = resolvedValue;
        this.expression = expression;
    }

    @Override
    public @Nullable T resolve(@NotNull BuildContext context) {

        if (this.getResolvedValue() != null) {
            return this.getResolvedValue();
        }

        String expression = this.getExpression();
        if (expression == null) {
            return null;
        }

        Player player = context.getPlayer();

        String parsedValue = Placeholder.Placeholders.getPlaceholder()
                .setPlaceholders(
                        player,
                        context.getPlaceholders().parse(expression)
                );

        return this.parse(parsedValue);
    }

    public boolean isDynamic() {
        return this.expression != null;
    }

    public @Nullable T getResolvedValue() {
        return this.resolvedValue;
    }

    public @Nullable String getExpression() {
        return this.expression;
    }

    protected abstract @Nullable T parse(@NotNull String value);

    /**
     * Writes the placeholder expression as written, or the resolved value through {@link #serializeValue}.
     */
    @Override
    public @Nullable Object serialize() {
        if (this.expression != null) return this.expression;
        return this.serializeValue(Objects.requireNonNull(this.resolvedValue));
    }

    /**
     * Writes a resolved value the way {@link #parse} reads it back. Numbers, booleans and strings
     * are written as they are, enums by name, registry entries and keys by their key; other types
     * must override this.
     *
     * @param value The resolved value.
     * @return A plain value, a list or a map, ready to be written in YAML.
     * @throws UnsupportedOperationException If the value has no configuration form.
     */
    protected @Nullable Object serializeValue(@NotNull T value) {
        if (value instanceof Number || value instanceof Boolean || value instanceof String) return value;
        return switch (value) {
            case Enum<?> enumValue -> enumValue.name();
            case NamespacedKey key -> key.toString();
            case Keyed keyed -> keyed.getKey().toString();
            default -> throw new UnsupportedOperationException(this.getClass().getName() + " cannot serialize the value " + value);
        };
    }

    protected static <T, R extends ParsableResolvable<T>> @NotNull R auto(
            @NotNull String value,
            @NotNull Function<String, T> parser,
            @NotNull BiFunction<T, String, R> factory
    ) {
        if (Resolvable.isExpression(value)) {
            return factory.apply(null, value);
        }
        try {
            T parsed = parser.apply(value);
            return factory.apply(parsed, null);
        } catch (Exception e) {
            return factory.apply(null, value);
        }
    }

    protected static <T, R extends ParsableResolvable<T>> @NotNull R auto(
            @Nullable String value,
            @NotNull T defaultValue,
            @NotNull Function<String, T> parser,
            @NotNull BiFunction<T, String, R> factory
    ) {
        if (value == null) {
            return factory.apply(defaultValue, null);
        }
        return auto(value, parser, factory);
    }

    protected static <T, R extends ParsableResolvable<T>> @Nullable R autoOrNull(
            @Nullable String value,
            @NotNull Function<String, T> parser,
            @NotNull BiFunction<T, String, R> factory
    ) {
        if (value == null) return null;
        return auto(value, parser, factory);
    }
}
