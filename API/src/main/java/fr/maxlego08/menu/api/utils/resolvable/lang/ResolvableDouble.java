package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ResolvableDouble extends ParsableResolvable<Double> {

    private ResolvableDouble(@Nullable Double resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableDouble of(double value) {
        return new ResolvableDouble(value, null);
    }

    public static @NotNull ResolvableDouble of(@NotNull String expression) {
        return new ResolvableDouble(null, expression);
    }

    public static @NotNull ResolvableDouble auto(@NotNull String value) {
        return auto(value, Double::parseDouble, ResolvableDouble::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableDouble autoOrNull(@Nullable String value) {
        return autoOrNull(value, Double::parseDouble, ResolvableDouble::new);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableDouble of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number number) {
            return new ResolvableDouble(number.doubleValue(), null);
        } else if (value instanceof String strValue) {
            return new ResolvableDouble(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableDouble(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable Double parse(@NotNull String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
