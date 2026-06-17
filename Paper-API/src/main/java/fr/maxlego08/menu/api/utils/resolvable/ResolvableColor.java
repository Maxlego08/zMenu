package fr.maxlego08.menu.api.utils.resolvable;

import fr.maxlego08.menu.api.utils.ColorUtils;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableColor extends Resolvable<Color> {

    private ResolvableColor(@Nullable Color resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @Nullable ResolvableColor of(@NotNull Object raw) {

        Color color = ColorUtils.parse(raw);
        if (color != null) {
            return new ResolvableColor(color, null);
        }

        if (raw instanceof String expression && !expression.isBlank()) {
            return new ResolvableColor(null, expression);
        }

        return null;
    }

    public static @NotNull ResolvableColor of(@NotNull Color color) {
        return new ResolvableColor(color, null);
    }

    public static @NotNull ResolvableColor ofExpression(@NotNull String expression) {
        return new ResolvableColor(null, expression);
    }

    @Override
    protected @Nullable Color parse(@NotNull String value) {
        return ColorUtils.parse(value);
    }
}