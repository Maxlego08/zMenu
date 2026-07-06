package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class TypedKeyResolvable<T extends Keyed> extends ParsableResolvable<TypedKey<T>> {

    private final RegistryKey<T> registryKey;

    private TypedKeyResolvable(
            @Nullable TypedKey<T> resolvedValue,
            @Nullable String expression,
            @NotNull RegistryKey<T> registryKey
    ) {
        super(resolvedValue, expression);
        this.registryKey = registryKey;
    }

    static <T extends Keyed> @NotNull TypedKeyResolvable<T> auto(
            @NotNull String value,
            @NotNull RegistryKey<T> registryKey
    ) {
        Function<String, TypedKey<T>> parser = v -> {
            NamespacedKey key = NamespacedKey.fromString(v);
            if (key == null) return null;
            return TypedKey.create(registryKey, key);
        };
        BiFunction<TypedKey<T>, String, TypedKeyResolvable<T>> factory =
                (resolved, expr) -> new TypedKeyResolvable<>(resolved, expr, registryKey);
        return auto(value, parser, factory);
    }

    @Override
    protected @Nullable TypedKey<T> parse(@NotNull String value) {
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) return null;
        return TypedKey.create(this.registryKey, key);
    }
}