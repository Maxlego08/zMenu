package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableIntArray extends ParsableResolvable<int[]> {

    private ResolvableIntArray(@Nullable int[] resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableIntArray of(@NotNull int[] value) {
        return new ResolvableIntArray(value, null);
    }

    public static @NotNull ResolvableIntArray of(@NotNull String expression) {
        return new ResolvableIntArray(null, expression);
    }

    public static @NotNull ResolvableIntArray auto(@NotNull String value) {
        return auto(value, ResolvableIntArray::parseArray, ResolvableIntArray::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableIntArray autoOrNull(@Nullable String value) {
        return autoOrNull(value, ResolvableIntArray::parseArray, ResolvableIntArray::new);
    }

    @Override
    protected @Nullable int[] parse(@NotNull String value) {
        try {
            return parseArray(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    private static int[] parseArray(@NotNull String value) {
        String[] parts = value.split(",");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Integer.parseInt(parts[i].trim());
        }
        return arr;
    }
}
