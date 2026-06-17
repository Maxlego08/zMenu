package fr.maxlego08.menu.api.utils.resolvable;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Resolvable<T> {

    private final @Nullable T resolvedValue;
    private final @Nullable String expression;

    protected Resolvable(@Nullable T resolvedValue, @Nullable String expression) {

        if ((resolvedValue == null) == (expression == null)) {
            throw new IllegalArgumentException(
                    "Exactly one of resolvedValue or expression must be non-null"
            );
        }

        this.resolvedValue = resolvedValue;
        this.expression = expression;
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

    public @Nullable T resolve(@NotNull BuildContext context) {

        if (this.resolvedValue != null) {
            return this.resolvedValue;
        }

        String expression = this.expression;
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

    protected abstract @Nullable T parse(@NotNull String value);
}