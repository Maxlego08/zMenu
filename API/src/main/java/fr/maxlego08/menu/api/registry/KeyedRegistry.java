package fr.maxlego08.menu.api.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

@ApiStatus.NonExtendable
public interface KeyedRegistry<T extends Keyed> extends Iterable<T> {

    @NotNull RegistryType<T> type();

    @NotNull Optional<T> get(@NotNull Key id);

    default @NotNull T getOrThrow(@NotNull Key id) {
        return this.get(id).orElseThrow(() -> new NoSuchElementException("No entry " + id.asString() + " in registry " + this.type()));
    }

    boolean contains(@NotNull Key id);

    boolean containsValue(@NotNull T value);

    /**
     * @return the name of what registered {@code id}, if it exists
     */
    @NotNull Optional<String> owner(@NotNull Key id);

    /**
     * @return the other names of entries, from the alias to the entry it points to
     */
    default @NotNull Map<Key, Key> aliases() {
        return Map.of();
    }

    @NotNull Set<Key> keys();

    @NotNull List<T> values();

    default @NotNull Stream<T> stream() {
        return this.values().stream();
    }

    default int size() {
        return this.values().size();
    }

    @Override
    default @NotNull Iterator<T> iterator() {
        return this.values().iterator();
    }
}
