package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;

import java.io.File;

/**
 * A loader for creating instances of {@link Action} based on configuration.
 */
public interface ActionLoader {

    /**
     * Gets the key that defines the type of action.
     *
     * @return The key.
     */
    String getKey();

    /**
     * Creates an instance of {@link Action} based on the provided configuration.
     *
     * @param path      The path in the configuration file.
     * @param accessor  The map accessor containing the configuration elements.
     * @param file      The file where the configuration is located.
     * @return The created {@link Action}.
     */
    Action load(String path, TypedMapAccessor accessor, File file);
}
