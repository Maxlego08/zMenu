package fr.maxlego08.menu.api.registry;

import fr.maxlego08.menu.api.registry.reload.ReloadReport;
import fr.maxlego08.menu.api.registry.reload.ReloadValidator;
import fr.maxlego08.menu.api.registry.reload.ReloadableProvider;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Every registry of zMenu. Read them as a {@link RegistryAccess}, and register your own entries
 * with {@link #writable(RegistryType)}.
 *
 * <p>Get it with {@link fr.maxlego08.menu.api.InventoryManager#getRegistries()}. Static registries
 * stop accepting entries once the server has finished loading, so register from your
 * {@code onEnable}.</p>
 */
@ApiStatus.NonExtendable
public interface RegistryManager extends RegistryAccess {

    /**
     * Gets the write side of a registry.
     *
     * @param type the registry
     * @param <T>  the type of its entries
     * @return the registry, to register entries in
     * @throws IllegalArgumentException if no such registry exists
     */
    <T extends Keyed> @NotNull WritableRegistry<T> writable(@NotNull RegistryType<T> type);

    /**
     * Adds a provider to a reloadable registry. It runs on the next {@link #reload(RegistryType)}.
     *
     * @param type     the reloadable registry
     * @param owner    the name of what provides, shown in reports
     * @param provider the provider
     * @throws IllegalArgumentException if no such reloadable registry exists
     */
    <T extends Keyed> void provider(@NotNull RegistryType<T> type, @NotNull String owner, @NotNull ReloadableProvider<T> provider);

    /**
     * Adds a check run on every new snapshot, before it is published.
     *
     * @throws IllegalArgumentException if no such reloadable registry exists
     */
    <T extends Keyed> void validator(@NotNull RegistryType<T> type, @NotNull String owner, @NotNull ReloadValidator<T> validator);

    /**
     * Rebuilds a reloadable registry from its providers. The new snapshot replaces the live one
     * only if nothing reported an error; otherwise the previous snapshot stays live.
     *
     * @param type the reloadable registry
     * @return what was found
     * @throws IllegalArgumentException if no such reloadable registry exists
     */
    @NotNull ReloadReport reload(@NotNull RegistryType<?> type);

    /**
     * Reloads every reloadable registry, in the order they were created.
     *
     * @return one report per registry
     */
    @NotNull List<ReloadReport> reloadAll();
}
