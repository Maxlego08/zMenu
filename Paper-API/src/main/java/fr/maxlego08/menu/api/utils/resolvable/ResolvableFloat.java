package fr.maxlego08.menu.api.utils.resolvable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableFloat extends Resolvable<Float> {

    private ResolvableFloat(@Nullable Float resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableFloat of(float value) {
        return new ResolvableFloat(value, null);
    }

    public static @NotNull ResolvableFloat of(@NotNull String expression) {
        return new ResolvableFloat(null, expression);
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