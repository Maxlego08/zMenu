package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class BukkitRegistries {

    private BukkitRegistries() {}

    @NotNull
    public static <T extends Keyed> Function<NamespacedKey, @Nullable T> resolverFor(
            @NotNull Class<T> type
    ) {
        Registry<T> registry = Bukkit.getRegistry(type);
        if (registry == null) throw new IllegalArgumentException("No Bukkit registry for " + type.getName());
        return key -> {
            try {
                return registry.get(key);
            } catch (Exception e) {
                return null;
            }
        };
    }

    @NotNull
    public static <T extends Keyed> RegistryEntry<T> auto(
            @NotNull String value,
            @NotNull Class<T> type
    ) {
        return RegistryEntry.auto(value, resolverFor(type));
    }

    @Nullable
    public static <T extends Keyed> RegistryEntry<T> autoOrNull(
            @Nullable String value,
            @NotNull Class<T> type
    ) {
        return RegistryEntry.autoOrNull(value, resolverFor(type));
    }

    @NotNull
    public static <T extends Keyed> RegistryEntry<T> ofValue(
            @NotNull T value,
            @NotNull Class<T> type
    ) {
        return RegistryEntry.ofValue(value, resolverFor(type));
    }

    @NotNull
    public static <T extends Keyed> RegistryEntry<T> ofExpression(
            @NotNull String expression,
            @NotNull Class<T> type
    ) {
        return RegistryEntry.ofExpression(expression, resolverFor(type));
    }
}