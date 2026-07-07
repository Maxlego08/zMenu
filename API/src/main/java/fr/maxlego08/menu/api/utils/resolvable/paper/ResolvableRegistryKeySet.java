package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResolvableRegistryKeySet {

    private ResolvableRegistryKeySet() {}

    @NotNull
    public static <T extends Keyed> TypedKeySetResolvable<T> typedKeySet(
            @NotNull RegistryKey<T> registryKey,
            @NotNull Object value
    ) {
        if (value instanceof String string) {
            return typedKeySet(registryKey, Collections.singletonList(string));
        }
        if (value instanceof List<?> list) {
            List<String> strings = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item != null) strings.add(item.toString());
            }
            return typedKeySet(registryKey, strings);
        }
        throw new IllegalArgumentException("Expected String or List, got " + value.getClass());
    }

    @NotNull
    public static <T extends Keyed> TypedKeySetResolvable<T> typedKeySet(
            @NotNull RegistryKey<T> registryKey,
            @NotNull String value
    ) {
        return typedKeySet(registryKey, Collections.singletonList(value));
    }

    @NotNull
    public static <T extends Keyed> TypedKeySetResolvable<T> typedKeySet(
            @NotNull RegistryKey<T> registryKey,
            @NotNull List<String> values
    ) {
        List<Resolvable<TypedKey<T>>> keys = new ArrayList<>(values.size());
        for (String value : values) {
            keys.add(ResolvableRegistryKey.typedKey(registryKey, value));
        }
        return new TypedKeySetResolvable<>(registryKey, keys);
    }

    @Nullable
    @Contract("_, null -> null; _, !null -> !null")
    public static <T extends Keyed> TypedKeySetResolvable<T> typedKeySetOrNull(
            @NotNull RegistryKey<T> registryKey,
            @Nullable Object value
    ) {
        if (value == null) return null;
        return typedKeySet(registryKey, value);
    }

    @Nullable
    @Contract("_, null -> null; _, !null -> !null")
    public static <T extends Keyed> Resolvable<RegistryKeySet<T>> typedKeySetOrNull(
            @NotNull RegistryKey<T> registryKey,
            @Nullable List<String> values
    ) {
        if (values == null || values.isEmpty()) return null;
        return typedKeySet(registryKey, values);
    }
}
