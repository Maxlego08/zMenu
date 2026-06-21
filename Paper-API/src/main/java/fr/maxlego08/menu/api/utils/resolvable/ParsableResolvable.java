package fr.maxlego08.menu.api.utils.resolvable;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ParsableResolvable<T> extends Resolvable<T> {
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

    protected static <T, R extends ParsableResolvable<T>> @NotNull R auto(
            @NotNull String value,
            @NotNull Function<String, T> parser,
            @NotNull BiFunction<T, String, R> factory
    ) {
        try {
            T parsed = parser.apply(value);
            return factory.apply(parsed, null);
        } catch (Exception e) {
            return factory.apply(null, value);
        }
    }

    protected static <T, R extends ParsableResolvable<T>> @Nullable R autoOrNull(
            @Nullable String value,
            @NotNull Function<String, T> parser,
            @NotNull BiFunction<T, String, R> factory
    ) {
        if (value == null || value.isBlank()) return null;
        return auto(value, parser, factory);
    }
}
