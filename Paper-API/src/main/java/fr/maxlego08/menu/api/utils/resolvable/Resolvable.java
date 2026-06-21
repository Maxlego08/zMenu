package fr.maxlego08.menu.api.utils.resolvable;

import fr.maxlego08.menu.api.context.BuildContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Resolvable<T> {

    protected Resolvable() {
    }

    public abstract @Nullable T resolve(@NotNull BuildContext context);

    protected static boolean isExpression(@NotNull String toResolve) {
        return toResolve.contains("%");
    }

    public static <X> void applyResolvable(@NotNull BuildContext context, @Nullable Resolvable<X> resolvable, @NotNull Consumer<X> consumer) {
        if (resolvable == null) {
            return;
        }

        X value = resolvable.resolve(context);
        if (value != null) {
            consumer.accept(value);
        }
    }

    public static <X> void applyResolvable(@NotNull BuildContext context, @Nullable List<? extends @Nullable Resolvable<X>> resolvables, @NotNull Consumer<List<X>> consumer) {
        if (resolvables == null) {
            return;
        }
        List<X> values = new ArrayList<>();
        for (Resolvable<X> resolvable : resolvables) {
            if (resolvable != null) {
                X value = resolvable.resolve(context);
                if (value != null) {
                    values.add(value);
                }
            }
        }
        if (!values.isEmpty()) {
            consumer.accept(values);
        }
    }

    public static <X> @Nullable X resolve(@NotNull BuildContext context, @Nullable Resolvable<X> resolvable) {
        if (resolvable == null) {
            return null;
        }
        return resolvable.resolve(context);
    }
}