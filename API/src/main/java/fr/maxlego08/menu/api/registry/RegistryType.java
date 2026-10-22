package fr.maxlego08.menu.api.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public record RegistryType<T extends Keyed>(@NotNull Key key, @NotNull Class<? super T> valueType, @NotNull Kind kind) implements Keyed {

    public enum Kind {
        /** Filled during registration, then frozen for the server lifetime. */
        STATIC,
        /** Rebuilt from providers on every reload and swapped atomically. */
        RELOADABLE
    }

    public static <T extends Keyed> @NotNull RegistryType<T> createStatic(@NotNull Key key, @NotNull Class<? super T> valueType) {
        return new RegistryType<>(key, valueType, Kind.STATIC);
    }

    public static <T extends Keyed> @NotNull RegistryType<T> createReloadable(@NotNull Key key, @NotNull Class<? super T> valueType) {
        return new RegistryType<>(key, valueType, Kind.RELOADABLE);
    }

    public boolean isReloadable() {
        return this.kind == Kind.RELOADABLE;
    }

    @Override
    public @NotNull String toString() {
        return this.key.asString();
    }
}
