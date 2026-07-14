package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.ColorUtils;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public final class ResolvableDyeColor extends ResolvableEnum<DyeColor> {

    private ResolvableDyeColor(@Nullable DyeColor resolvedValue, @Nullable String expression) {
        super(DyeColor.class, resolvedValue, expression);
    }

    public static @NotNull ResolvableDyeColor of(@NotNull DyeColor value) {
        return new ResolvableDyeColor(value, null);
    }

    public static @NotNull ResolvableDyeColor of(@NotNull String expression) {
        return new ResolvableDyeColor(null, expression);
    }

    public static @NotNull ResolvableDyeColor auto(@NotNull String toResolve) {
        if (Resolvable.isExpression(toResolve)) {
            return ResolvableDyeColor.of(toResolve);
        }
        DyeColor color = parseColor(toResolve);
        if (color == null) {
            throw new IllegalArgumentException("Invalid color value: " + toResolve);
        }
        return new ResolvableDyeColor(color, null);
    }

    @Contract("null -> null; !null -> !null")
    public static @org.jetbrains.annotations.Nullable ResolvableDyeColor autoOrNull(@Nullable String toResolve) {
        if (toResolve == null) {
            return null;
        }
        return auto(toResolve);
    }

    public static @Nullable ResolvableDyeColor autoOrNull(@Nullable Object toResolve) {
        if (toResolve == null) {
            return null;
        }
        if (toResolve instanceof String str) {
            return autoOrNull(str);
        }
        DyeColor color = parseColor(toResolve);
        if (color == null) {
            return null;
        }
        return new ResolvableDyeColor(color, null);
    }

    @Override
    protected @Nullable DyeColor parse(@NotNull String value) {
        return parseColor(value);
    }

    private static @org.jetbrains.annotations.Nullable DyeColor parseColor(@NotNull Object value) {
        Color color = ColorUtils.parse(value);
        if (color == null) {
            return null;
        }
        return DyeColor.getByColor(color);
    }
}
