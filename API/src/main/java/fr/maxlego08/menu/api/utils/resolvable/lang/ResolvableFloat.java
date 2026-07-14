package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableFloat extends ParsableResolvable<Float> {

    private ResolvableFloat(@Nullable Float resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableFloat of(float value) {
        return new ResolvableFloat(value, null);
    }

    public static @NotNull ResolvableFloat of(@NotNull String expression) {
        return new ResolvableFloat(null, expression);
    }

    public static @NotNull ResolvableFloat auto(@NotNull String value) {
        return auto(value, Float::parseFloat, ResolvableFloat::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableFloat autoOrNull(@Nullable String value) {
        return autoOrNull(value, Float::parseFloat, ResolvableFloat::new);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableFloat of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Float defaultValue) {
        Object value = map.get(key);
        if (value instanceof Float floatValue) {
            return new ResolvableFloat(floatValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableFloat(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableFloat(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable Float parse(@NotNull String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}