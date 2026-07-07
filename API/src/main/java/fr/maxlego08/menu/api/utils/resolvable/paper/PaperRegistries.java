package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class PaperRegistries {

    private PaperRegistries() {}

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
    public static <T extends Keyed> ResolvableRegistryEntry<T> autoOrNull(
            @Nullable String value,
            @NotNull RegistryKey<T> registryKey
    ) {
        return ResolvableRegistryEntry.autoOrNull(value, resolverFor(registryKey));
    }

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> ofValue(
            @NotNull T value,
            @NotNull RegistryKey<T> registryKey
    ) {
        return ResolvableRegistryEntry.ofValue(value, resolverFor(registryKey));
    }

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> ofExpression(
            @NotNull String expression,
            @NotNull RegistryKey<T> registryKey
    ) {
        return ResolvableRegistryEntry.ofExpression(expression, resolverFor(registryKey));
    }
}