package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableString extends ParsableResolvable<String> {

    private ResolvableString(@Nullable String resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableString of(@NotNull String value) {
        return new ResolvableString(value, null);
    }

    public static @NotNull ResolvableString ofExpression(@NotNull String expression) {
        return new ResolvableString(null, expression);
    }

    public static @NotNull ResolvableString auto(@NotNull String toResolve) {
        if (Resolvable.isExpression(toResolve)) {
            return new ResolvableString(null, toResolve);
        }
        return new ResolvableString(toResolve, null);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableString autoOrNull(@Nullable String toResolve) {
        if (toResolve == null) return null;
        return auto(toResolve);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableString of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable String defaultValue) {
        Object value = map.get(key);
        if (value instanceof String strValue) {
            if (Resolvable.isExpression(strValue)) {
                return new ResolvableString(null, strValue);
            }
            return new ResolvableString(strValue, null);
        }
        return defaultValue != null ? new ResolvableString(defaultValue, null) : null;
    }

    @Override
    protected @Nullable String parse(@NotNull String value) {
        return value;
    }
}
