package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ResolvableRegistry {

    private ResolvableRegistry() {}

    @NotNull
    public static <T extends Keyed> Function<NamespacedKey, @Nullable T> resolverFor(
            @NotNull RegistryKey<T> registryKey
    ) {
        return key -> {
            try {
                return RegistryAccess.registryAccess().getRegistry(registryKey).get(key);
            } catch (Exception e) {
                return null;
            }
        };
    }

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> auto(
            @NotNull String value,
            @NotNull RegistryKey<T> registryKey
    ) {
        return ResolvableRegistryEntry.auto(value, resolverFor(registryKey));
    }

    @Nullable
    @Contract("null, _ -> null; !null, _ -> !null")
    public static <T extends Keyed> ResolvableRegistryEntry<T> autoOrNull(
            @Nullable String value,
            @NotNull RegistryKey<T> registryKey
    ) {
        if (value == null) return null;
        return auto(value, registryKey);
    }

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> ofValue(
            @NotNull T value,
            @NotNull RegistryKey<T> registryKey
    ) {
        return ResolvableRegistryEntry.ofValue(value, resolverFor(registryKey));
    }

    @Nullable
    public static <T extends Keyed> ResolvableRegistryEntry<T> ofRegisteredOrNull(
            @NotNull T value,
            @NotNull RegistryKey<T> registryKey
    ) {
        try {
            Registry<T> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
            NamespacedKey key = registry.getKey(value);
            if (key == null || !value.equals(registry.get(key))) return null;
        } catch (Exception e) {
            return null;
        }
        return ofValue(value, registryKey);
    }

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> ofExpression(
            @NotNull String expression,
            @NotNull RegistryKey<T> registryKey
    ) {
        return ResolvableRegistryEntry.ofExpression(expression, resolverFor(registryKey));
    }
}
