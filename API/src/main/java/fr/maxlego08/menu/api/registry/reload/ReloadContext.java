package fr.maxlego08.menu.api.registry.reload;

import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistryAccess;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Given to a {@link ReloadableProvider} while a new snapshot is being built.
 *
 * @param <T> the type of the entries
 */
@ApiStatus.NonExtendable
public interface ReloadContext<T extends Keyed> extends ReloadReporter {

    /**
     * @return the name of the provider being run
     */
    @NotNull String owner();

    /**
     * @return the registries, where the one being reloaded reads the snapshot under construction
     */
    @NotNull RegistryAccess registries();

    /**
     * Adds an entry to the new snapshot. A key that is already taken is reported as an error.
     *
     * @param value the entry
     * @return the entry
     */
    <V extends T> @NotNull V register(@NotNull V value);

    /**
     * Gets the snapshot that is live while this one is being built, to keep an entry whose file
     * cannot be read anymore.
     *
     * @return the live snapshot
     */
    @NotNull KeyedRegistry<T> previous();

    /**
     * Runs an action once this snapshot has replaced the live one, such as firing a load event.
     * The action never runs if the snapshot is rejected.
     *
     * @param action the action
     */
    void onPublish(@NotNull Runnable action);
}
