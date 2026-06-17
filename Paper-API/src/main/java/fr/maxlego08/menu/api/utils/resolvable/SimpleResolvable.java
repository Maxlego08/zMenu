package fr.maxlego08.menu.api.utils.resolvable;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class SimpleResolvable<T> extends Resolvable<T> {
    private final Function<String, T> parser;

    private SimpleResolvable(@Nullable T resolvedValue, @Nullable String expression, @NotNull Function<String, T> parser) {
        super(resolvedValue, expression);
        this.parser = parser;
    }

    public static <T> @NotNull SimpleResolvable<T> of(@NotNull T value, @NotNull Function<String, T> parser) {
        return new SimpleResolvable<>(value, null, parser);
    }

    public static <T> @NotNull SimpleResolvable<T> ofExpression(@NotNull String expression, @NotNull Function<String, T> parser) {
        return new SimpleResolvable<>(null, expression, parser);
    }

    @Override
    protected @Nullable T parse(@NotNull String value) {
        try {
            return this.parser.apply(value);
        } catch (Exception exception) {
            return null;
        }
    }
}
