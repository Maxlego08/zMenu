package fr.maxlego08.menu.api.utils.resolvable;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.context.BuildContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Resolvable<T> {

    @Nullable T resolve(@NotNull BuildContext context);

    /**
     * Returns this value in the form the configuration holds it, so a component loader reading it
     * back builds the same resolvable: the placeholder expression as written, or the value itself.
     *
     * @return A plain value, a list or a map, ready to be written in YAML.
     * @throws UnsupportedOperationException If this resolvable cannot be written back.
     */
    default @Nullable Object serialize() {
        throw new UnsupportedOperationException(this.getClass().getName() + " cannot be serialized");
    }

    /**
     * Serializes a list of resolvables, in order; a null entry stays null.
     */
    static @NotNull List<Object> serializeList(@NotNull List<? extends @Nullable Resolvable<?>> resolvables) {
        List<Object> values = new ArrayList<>(resolvables.size());
        for (Resolvable<?> resolvable : resolvables) {
            values.add(resolvable == null ? null : resolvable.serialize());
        }
        return values;
    }

    static boolean isExpression(@NotNull String toResolve) {
        int first = toResolve.indexOf('%');
        return first != -1 && toResolve.indexOf('%', first + 1) != -1;
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

    static <X> @NotNull X resolveOrDefault(
            @NotNull BuildContext context,
            @Nullable Resolvable<X> resolvable,
            @NotNull X defaultValue
    ) {
        Preconditions.checkNotNull(defaultValue, "Default value cannot be null");
        if (resolvable == null) {
            return defaultValue;
        }

        X value = resolvable.resolve(context);
        return value != null ? value : defaultValue;
    }

    static <X> @NotNull List<X> resolveList(
            @NotNull BuildContext context,
            @Nullable List<? extends @Nullable Resolvable<X>> resolvables
    ) {
        List<X> values = new ArrayList<>();

        if (resolvables == null) {
            return values;
        }

        for (Resolvable<X> resolvable : resolvables) {
            if (resolvable != null) {
                X value = resolvable.resolve(context);

                if (value != null) {
                    values.add(value);
                }
            }
        }

        return values;
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