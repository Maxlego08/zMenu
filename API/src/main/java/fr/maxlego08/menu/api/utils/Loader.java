package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface Loader<T> {

    /**
     * Loads an object from a YAML configuration.
     *
     * @param configuration The YAML configuration to load the object from.
     * @param path The path within the configuration to locate the object.
     * @param objects Additional parameters that might be needed for loading.
     * @return The loaded object.
     * @throws InventoryException If there is an error while loading the object.
     */
    T load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException;

    /**
     * Saves an object to a YAML configuration.
     *
     * @param object The object to be saved.
     * @param configuration The YAML configuration to save the object to.
     * @param path The path within the configuration where the object should be saved.
     * @param file The file where the configuration is stored.
     * @param objects Additional parameters that might be needed for saving.
     */
    void save(T object, YamlConfiguration configuration, String path, File file, Object... objects);

}
