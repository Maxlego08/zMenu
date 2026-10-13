package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Generic loader interface for loading/saving plugin objects out of YAML configuration files.
 *
 * @param <T> Type of object handled by this loader.
 */
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
    T load(@NotNull YamlConfiguration configuration, @NotNull String path, Object... objects) throws InventoryException;

    /**
     * Writes an object into a configuration section, in the same format {@link #load} reads.
     * <p>
     * By default the object serializes itself through {@link SectionSerializable}; prefer calling
     * that directly. A loader overrides this only when serializing needs something the object does
     * not know, or for types it cannot change.
     * <p>
     * This method never writes to disk, so serializers can call each other freely; the caller saves the file once.
     *
     * @param object The object to serialize.
     * @param section The section to write the object's keys into.
     * @param objects Additional parameters that might be needed for serializing.
     * @throws UnsupportedOperationException If the object cannot be serialized.
     */
    default void serialize(@NotNull T object, @NotNull ConfigurationSection section, Object... objects) {
        if (object instanceof SectionSerializable serializable) {
            serializable.serialize(section);
            return;
        }
        throw new UnsupportedOperationException(object.getClass().getName() + " cannot be serialized");
    }

    /**
     * Serializes an object at the given path, then saves the configuration to the file.
     *
     * @param object The object to be saved.
     * @param configuration The YAML configuration to save the object to.
     * @param path The path within the configuration where the object should be saved, empty for the root.
     * @param file The file where the configuration is stored.
     * @param objects Additional parameters that might be needed for saving.
     * @deprecated Use {@link #serialize} and save the configuration yourself.
     */
    @Deprecated
    default void save(T object, @NotNull YamlConfiguration configuration, @NotNull String path, File file, Object... objects) {
        String sectionPath = path.endsWith(".") ? path.substring(0, path.length() - 1) : path;
        ConfigurationSection section = sectionPath.isEmpty() ? configuration : configuration.createSection(sectionPath);
        this.serialize(object, section, objects);
        try {
            configuration.save(file);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
