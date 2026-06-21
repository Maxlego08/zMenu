package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableBoolean extends ParsableResolvable<Boolean> {

    private ResolvableBoolean(@Nullable Boolean resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableBoolean of(boolean value) {
        return new ResolvableBoolean(value, null);
    }

    public static @NotNull ResolvableBoolean of(@NotNull String expression) {
        return new ResolvableBoolean(null, expression);
    }

    public static @NotNull ResolvableBoolean auto(@NotNull String value) {
        return auto(value, Boolean::parseBoolean, ResolvableBoolean::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableBoolean autoOrNull(@Nullable String value) {
        return autoOrNull(value, Boolean::parseBoolean, ResolvableBoolean::new);
    }

    @Contract("_, _, !null -> !null")
    public static @Nullable ResolvableBoolean of(@NotNull java.util.Map<String, Object> map, @NotNull String key, @Nullable Boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean boolValue) {
            return new ResolvableBoolean(boolValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableBoolean(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableBoolean(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable Boolean parse(@NotNull String value) {
        return Boolean.parseBoolean(value);
    }
}
