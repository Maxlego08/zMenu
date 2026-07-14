package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableByte extends ParsableResolvable<Byte> {

    private ResolvableByte(@Nullable Byte resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableByte of(byte value) {
        return new ResolvableByte(value, null);
    }

    public static @NotNull ResolvableByte of(@NotNull String expression) {
        return new ResolvableByte(null, expression);
    }

    public static @NotNull ResolvableByte auto(@NotNull String value) {
        return auto(value, Byte::parseByte, ResolvableByte::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableByte autoOrNull(@Nullable String value) {
        return autoOrNull(value, Byte::parseByte, ResolvableByte::new);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableByte of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Byte defaultValue) {
        Object value = map.get(key);
        if (value instanceof Byte byteValue) {
            return new ResolvableByte(byteValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableByte(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableByte(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable Byte parse(@NotNull String value) {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
