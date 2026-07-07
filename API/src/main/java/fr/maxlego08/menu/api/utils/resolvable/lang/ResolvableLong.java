package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableLong extends ParsableResolvable<Long> {

    private ResolvableLong(@Nullable Long resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableLong of(long value) {
        return new ResolvableLong(value, null);
    }

    public static @NotNull ResolvableLong of(@NotNull String expression) {
        return new ResolvableLong(null, expression);
    }

    public static @NotNull ResolvableLong auto(@NotNull String value) {
        return auto(value, Long::parseLong, ResolvableLong::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableLong autoOrNull(@Nullable String value) {
        return autoOrNull(value, Long::parseLong, ResolvableLong::new);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableLong of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Long longValue) {
            return new ResolvableLong(longValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableLong(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableLong(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable Long parse(@NotNull String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
