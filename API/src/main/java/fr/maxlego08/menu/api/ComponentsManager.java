package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface ComponentsManager {

    void initializeDefaultComponents(MenuPlugin plugin);

    /**
     * Register a new ItemComponentLoader. A name already taken is logged and the loader is not registered.
     * @param loader The loader to register
     * @deprecated register it in {@link fr.maxlego08.menu.api.registry.Registries#ITEM_COMPONENT}:
     * {@code registries.writable(Registries.ITEM_COMPONENT).register(plugin, loader, loader.aliases())},
     * which throws on a name already taken.
     **/
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void registerComponent(@NotNull ItemComponentLoader loader);

    /**
     * Get an ItemComponentLoader by its name.
     * @param name The name of the loader
     * @return An Optional containing the loader if found, or empty if not found
     **/
    @NotNull
    Optional<ItemComponentLoader> getLoader(@NotNull String name);

    /**
     * @return Every registered loader, once each even when it has several names.
     */
    @NotNull
    Collection<ItemComponentLoader> getLoaders();
}
