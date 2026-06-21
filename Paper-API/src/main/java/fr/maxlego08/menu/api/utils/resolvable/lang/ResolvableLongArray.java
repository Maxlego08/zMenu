package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableLongArray extends ParsableResolvable<long[]> {

    private ResolvableLongArray(@Nullable long[] resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableLongArray of(@NotNull long[] value) {
        return new ResolvableLongArray(value, null);
    }

    public static @NotNull ResolvableLongArray of(@NotNull String expression) {
        return new ResolvableLongArray(null, expression);
    }

    public static @NotNull ResolvableLongArray auto(@NotNull String value) {
        return auto(value, ResolvableLongArray::parseArray, ResolvableLongArray::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableLongArray autoOrNull(@Nullable String value) {
        return autoOrNull(value, ResolvableLongArray::parseArray, ResolvableLongArray::new);
    }

    @Override
    protected @Nullable long[] parse(@NotNull String value) {
        try {
            return parseArray(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    private static long[] parseArray(@NotNull String value) {
        String[] parts = value.split(",");
        long[] arr = new long[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Long.parseLong(parts[i].trim());
        }
        return arr;
    }
}
