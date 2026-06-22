package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.PlatformType;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperRegistries;
import fr.maxlego08.menu.api.utils.resolvable.utils.RegistryKeys;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableRegistry {

    private ResolvableRegistry() {}

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> auto(
            @NotNull String value,
            @NotNull Class<T> type
    ) {
        if (PlatformType.isPaper()) {
            RegistryKey<T> key = RegistryKeys.forClass(type);
            if (key != null) return PaperRegistries.auto(value, key);
        }
        return BukkitRegistries.auto(value, type);
    }

    @Nullable
    @Contract("null, _ -> null; !null, _ -> !null")
    public static <T extends Keyed> ResolvableRegistryEntry<T> autoOrNull(
            @Nullable String value,
            @NotNull Class<T> type
    ) {
        if (value == null) return null;
        return auto(value, type);
    }

    @NotNull
    public static <T extends Keyed> ResolvableRegistryEntry<T> ofValue(
            @NotNull T value,
            @NotNull Class<T> type
    ) {
        if (PlatformType.isPaper()) {
            RegistryKey<T> key = RegistryKeys.forClass(type);
            if (key != null) return PaperRegistries.ofValue(value, key);
        }
        return BukkitRegistries.ofValue(value, type);
    }
}