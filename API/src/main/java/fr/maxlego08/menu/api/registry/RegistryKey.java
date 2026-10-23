package fr.maxlego08.menu.api.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RegistryKey<T extends Keyed>(@NotNull RegistryType<T> registry, @NotNull Key id) {

    public static <T extends Keyed> @NotNull RegistryKey<T> of(@NotNull RegistryType<T> registry, @NotNull Key id) {
        return new RegistryKey<>(registry, id);
    }

    public @NotNull Optional<T> resolve(@NotNull RegistryAccess access) {
        return access.registry(this.registry).get(this.id);
    }

    @Override
    public @NotNull String toString() {
        return this.registry + "/" + this.id.asString();
    }
}