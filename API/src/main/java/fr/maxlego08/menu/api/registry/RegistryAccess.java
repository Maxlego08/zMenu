package fr.maxlego08.menu.api.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface RegistryAccess {
    /**
     * @throws IllegalArgumentException if no such registry exists
     */
    <T extends Keyed> @NotNull KeyedRegistry<T> registry(@NotNull RegistryType<T> type);

    @NotNull Optional<KeyedRegistry<?>> find(@NotNull Key registryKey);

    @NotNull Collection<RegistryType<?>> types();
}
