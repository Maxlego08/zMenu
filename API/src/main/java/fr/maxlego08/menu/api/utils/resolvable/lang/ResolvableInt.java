package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableInt extends ParsableResolvable<Integer> {

    private ResolvableInt(@Nullable Integer resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableInt of(int value) {
        return new ResolvableInt(value, null);
    }

    public static @NotNull ResolvableInt of(@NotNull String expression) {
        return new ResolvableInt(null, expression);
    }

    public static @NotNull ResolvableInt auto(@NotNull String value) {
        return auto(value, Integer::parseInt, ResolvableInt::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableInt autoOrNull(@Nullable String value) {
        return autoOrNull(value, Integer::parseInt, ResolvableInt::new);
    }

    @Contract("_, _, _ -> !null")
    public static @Nullable ResolvableInt of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Integer defaultValue) {
        Object value = map.get(key);
        if (value instanceof Integer intValue) {
            return new ResolvableInt(intValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableInt(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableInt(defaultValue, null) : null;
        }

    }

    @Override
    protected @Nullable Integer parse(@NotNull String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
