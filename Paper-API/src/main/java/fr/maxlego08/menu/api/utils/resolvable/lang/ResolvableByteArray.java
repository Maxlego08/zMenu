package fr.maxlego08.menu.api.utils.resolvable.lang;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableByteArray extends ParsableResolvable<byte[]> {

    private ResolvableByteArray(@Nullable byte[] resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableByteArray of(@NotNull byte[] value) {
        return new ResolvableByteArray(value, null);
    }

    public static @NotNull ResolvableByteArray of(@NotNull String expression) {
        return new ResolvableByteArray(null, expression);
    }

    public static @NotNull ResolvableByteArray auto(@NotNull String value) {
        return auto(value, ResolvableByteArray::parseArray, ResolvableByteArray::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableByteArray autoOrNull(@Nullable String value) {
        return autoOrNull(value, ResolvableByteArray::parseArray, ResolvableByteArray::new);
    }

    @Override
    protected @Nullable byte[] parse(@NotNull String value) {
        try {
            return parseArray(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    private static byte[] parseArray(@NotNull String value) {
        String[] parts = value.split(",");
        byte[] arr = new byte[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Byte.parseByte(parts[i].trim());
        }
        return arr;
    }
}
