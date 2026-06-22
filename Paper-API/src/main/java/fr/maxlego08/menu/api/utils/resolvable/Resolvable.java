package fr.maxlego08.menu.api.utils.resolvable;

import fr.maxlego08.menu.api.context.BuildContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Resolvable<T> {

    @Nullable T resolve(@NotNull BuildContext context);

    static boolean isExpression(@NotNull String toResolve) {
        return toResolve.contains("%");
    }

    static <X> void applyResolvable(@NotNull BuildContext context, @Nullable Resolvable<X> resolvable, @NotNull Consumer<X> consumer) {
        if (resolvable == null) {
            return;
        }

        X value = resolvable.resolve(context);
        if (value != null) {
            consumer.accept(value);
        }
    }

    static <X> void applyResolvable(
            @NotNull BuildContext context,
            @Nullable List<? extends @Nullable Resolvable<X>> resolvables,
            @NotNull Consumer<List<X>> consumer) {
        applyResolvable(context, resolvables, ArrayList::new, consumer);
    }

    static <X, C extends Collection<X>> void applyResolvable(
            @NotNull BuildContext context,
            @Nullable List<? extends @Nullable Resolvable<X>> resolvables,
            @NotNull Supplier<C> collectionFactory,
            @NotNull Consumer<C> consumer) {
        if (resolvables == null) return;

        C values = collectionFactory.get();
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

    static <K, V> void applyResolvable(
            @NotNull BuildContext context,
            @Nullable Map<K, ? extends @Nullable Resolvable<V>> resolvables,
            @NotNull Consumer<Map<K, V>> consumer
    ) {
        if (resolvables == null) {
            return;
        }

        Map<K, V> values = resolveMap(context, resolvables);

        if (!values.isEmpty()) {
            consumer.accept(values);
        }
    }

    static <X> @Nullable X resolve(@NotNull BuildContext context, @Nullable Resolvable<X> resolvable) {
        if (resolvable == null) {
            return null;
        }
        return resolvable.resolve(context);
    }

    static <K, V> @NotNull Map<K, V> resolveMap(
            @NotNull BuildContext context,
            @Nullable Map<K, ? extends @Nullable Resolvable<V>> resolvables
    ) {
        Map<K, V> values = new HashMap<>();

        if (resolvables == null) {
            return values;
        }

        for (Map.Entry<K, ? extends Resolvable<V>> entry : resolvables.entrySet()) {
            Resolvable<V> resolvable = entry.getValue();

            if (resolvable != null) {
                V value = resolvable.resolve(context);

                if (value != null) {
                    values.put(entry.getKey(), value);
                }
            }
        }

        return values;
    }
}