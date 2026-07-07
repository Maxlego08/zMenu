package fr.maxlego08.menu.api.utils.resolvable.paper;

import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableRegistryKey {

    private ResolvableRegistryKey() {}

    @NotNull
    public static <T extends Keyed> TypedKeyResolvable<T> typedKey(
            @NotNull RegistryKey<T> registryKey,
            @NotNull String value
    ) {
        return TypedKeyResolvable.auto(value, registryKey);
    }

    @Nullable
    @Contract("_, null -> null; _, !null -> !null")
    public static <T extends Keyed> TypedKeyResolvable<T> typedKeyOrNull(
            @NotNull RegistryKey<T> registryKey,
            @Nullable String value
    ) {
        if (value == null) return null;
        return typedKey(registryKey, value);
    }

    @NotNull
    public static <T extends Keyed> TagKeyResolvable<T> tagKey(
            @NotNull RegistryKey<T> registryKey,
            @NotNull String value
    ) {
        return TagKeyResolvable.auto(value, registryKey);
    }

    @Nullable
    @Contract("_, null -> null; _, !null -> !null")
    public static <T extends Keyed> TagKeyResolvable<T> tagKeyOrNull(
            @NotNull RegistryKey<T> registryKey,
            @Nullable String value
    ) {
        if (value == null) return null;
        return tagKey(registryKey, value);
    }
}
