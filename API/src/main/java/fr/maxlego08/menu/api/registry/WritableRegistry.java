package fr.maxlego08.menu.api.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The write side of a {@link KeyedRegistry}, used while plugins register their entries.
 *
 * <p>A static registry stops accepting entries once the server has finished loading, so register
 * from your {@code onEnable}.</p>
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
     * Makes {@code from} another name of the entry {@code to}.
     *
     * @throws RegistrationException if the registry is frozen, {@code from} is taken, or {@code to} does not exist
     */
    void alias(@NotNull String owner, @NotNull Key from, @NotNull Key to);

    boolean contains(@NotNull Key id);
}
