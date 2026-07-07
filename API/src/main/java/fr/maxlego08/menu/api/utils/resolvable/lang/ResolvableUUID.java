package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public final class ResolvableUUID extends ParsableResolvable<UUID> {

    private ResolvableUUID(@Nullable UUID resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableUUID of(@NotNull UUID value) {
        return new ResolvableUUID(value, null);
    }

    public static @NotNull ResolvableUUID of(@NotNull String expression) {
        return new ResolvableUUID(null, expression);
    }

    public static @NotNull ResolvableUUID auto(@NotNull String value) {
        return auto(value, UUID::fromString, ResolvableUUID::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableUUID autoOrNull(@Nullable String value) {
        return autoOrNull(value, UUID::fromString, ResolvableUUID::new);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public static ResolvableUUID of(@NotNull Map<String, Object> map, @NotNull String key, @Nullable UUID defaultValue) {
        Object value = map.get(key);
        if (value instanceof UUID uuidValue) {
            return new ResolvableUUID(uuidValue, null);
        } else if (value instanceof String strValue) {
            return new ResolvableUUID(null, strValue);
        } else {
            return defaultValue != null ? new ResolvableUUID(defaultValue, null) : null;
        }
    }

    @Override
    protected @Nullable UUID parse(@NotNull String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
