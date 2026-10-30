package fr.maxlego08.menu.api.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The write side of a {@link KeyedRegistry}, used while plugins register their entries.
 *
 * <p>A static registry stops accepting entries once the server has finished loading, so register
 * from your {@code onEnable}. An {@link RegistryType.Kind#OPEN open} registry accepts entries at any
 * time and forgets those of a plugin when it disables.</p>
 *
 * @param <T> the type of the entries
 */
@ApiStatus.NonExtendable
public interface WritableRegistry<T extends Keyed> {

    @NotNull RegistryType<T> type();

    /**
     * Registers an entry under its own {@link Keyed#key() key}.
     *
     * @param owner the name of what registers it, shown when two entries clash
     * @param value the entry
     * @return the entry
     * @throws RegistrationException if the registry is frozen, the key is taken, or the value has the wrong type
     */
    <V extends T> @NotNull V register(@NotNull String owner, @NotNull V value);

    default <V extends T> @NotNull V register(@NotNull Plugin owner, @NotNull V value) {
        return this.register(owner.getName(), value);
    }

    /**
     * Registers an entry under its own key, and under each of its other names. A name equal to the
     * key, or given twice, is registered once. Either every name is registered, or none is.
     *
     * @param owner   the name of what registers it, shown when two entries clash
     * @param value   the entry
     * @param aliases the other names of the entry
     * @return the entry
     * @throws RegistrationException if the registry is frozen, a name is taken, or the value has the wrong type
     */
    default <V extends T> @NotNull V register(@NotNull String owner, @NotNull V value, @NotNull Collection<Key> aliases) {
        this.register(owner, value);
        try {
            aliases.stream().distinct().filter(alias -> !alias.equals(value.key())).forEach(alias -> this.alias(owner, alias, value.key()));
        } catch (RegistrationException exception) {
            this.unregister(value.key());
            throw exception;
        }
        return value;
    }

    default <V extends T> @NotNull V register(@NotNull Plugin owner, @NotNull V value, @NotNull Collection<Key> aliases) {
        return this.register(owner.getName(), value, aliases);
    }

    /**
     * Makes {@code from} another name of the entry {@code to}.
     *
     * @throws RegistrationException if the registry is frozen, {@code from} is taken, or {@code to} does not exist
     */
    void alias(@NotNull String owner, @NotNull Key from, @NotNull Key to);

    boolean contains(@NotNull Key id);

    /**
     * Removes an entry and every name pointing to it, or a single alias.
     *
     * @param id the key of the entry, or an alias
     * @return whether something was removed
     * @throws RegistrationException if the registry is frozen
     */
    boolean unregister(@NotNull Key id);

    /**
     * Removes every entry and alias registered by an owner.
     *
     * @param owner the name the entries were registered with
     * @return how many entries and aliases were removed
     * @throws RegistrationException if the registry is frozen
     */
    int unregisterAll(@NotNull String owner);

    default int unregisterAll(@NotNull Plugin owner) {
        return this.unregisterAll(owner.getName());
    }
}
