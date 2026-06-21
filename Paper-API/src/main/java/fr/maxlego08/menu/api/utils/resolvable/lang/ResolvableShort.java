package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableShort extends ParsableResolvable<Short> {

    private ResolvableShort(@Nullable Short resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableShort of(short value) {
        return new ResolvableShort(value, null);
    }

    public static @NotNull ResolvableShort of(@NotNull String expression) {
        return new ResolvableShort(null, expression);
    }

    public static @NotNull ResolvableShort auto(@NotNull String value) {
        return auto(value, Short::parseShort, ResolvableShort::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableShort autoOrNull(@Nullable String value) {
        return autoOrNull(value, Short::parseShort, ResolvableShort::new);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableShort of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Short defaultValue) {
        Object value = map.get(key);
        if (value instanceof Short shortValue) {
            return new ResolvableShort(shortValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableShort(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableShort(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable Short parse(@NotNull String value) {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
