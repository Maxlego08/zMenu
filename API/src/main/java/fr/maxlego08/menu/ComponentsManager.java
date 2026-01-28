package fr.maxlego08.menu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.ItemComponentAlreadyRegisterException;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ComponentsManager {

    void initializeDefaultComponents(MenuPlugin plugin);

    /**
     * Register a new ItemComponentLoader.
     * @param loader The loader to register
     * @throws ItemComponentAlreadyRegisterException if a loader with the same name is already registered
     **/
    void registerComponent(@NotNull ItemComponentLoader loader) throws ItemComponentAlreadyRegisterException;

    /**
     * Get an ItemComponentLoader by its name.
     * @param name The name of the loader
     * @return An Optional containing the loader if found, or empty if not found
     **/
    @NotNull
    Optional<ItemComponentLoader> getLoader(@NotNull String name);
}
