package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ResolvableEnum<E extends Enum<E>> extends ParsableResolvable<E> {
    private final @NotNull Class<E> enumClass;

    protected ResolvableEnum(@NotNull Class<E> enumClass, @Nullable E resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
        this.enumClass = enumClass;
    }

    private static @NotNull String normalize(String s) {
        return s.trim()
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase(Locale.ROOT);
    }

    public static @NotNull <E extends Enum<E>> ResolvableEnum<E> of(@NotNull Class<E> enumClass, @NotNull E value) {
        return new ResolvableEnum<>(enumClass, value, null);
    }

    public static @NotNull <E extends Enum<E>> ResolvableEnum<E> ofExpression(@NotNull Class<E> enumClass, @NotNull String expression) {
        return new ResolvableEnum<>(enumClass, null, expression);
    }

    public static @NotNull <E extends Enum<E>> ResolvableEnum<E> auto(@NotNull Class<E> enumClass, @NotNull String toResolve) {
        if (Resolvable.isExpression(toResolve)) {
            return new ResolvableEnum<>(enumClass, null, toResolve);
        }
        return auto(toResolve, s -> Enum.valueOf(enumClass, normalize(s)), (v, e) -> new ResolvableEnum<>(enumClass, v, e));
    }

    @Nullable
    @Contract("_, null -> null; _, !null -> !null")
    public static <E extends Enum<E>> ResolvableEnum<E> autoOrNull(@NotNull Class<E> enumClass, @Nullable String toResolve) {
        if (toResolve == null) return null;
        return auto(enumClass, toResolve);
    }

    @Override
    protected @Nullable E parse(@NotNull String value) {
        try {
            return Enum.valueOf(this.enumClass, normalize(value));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
