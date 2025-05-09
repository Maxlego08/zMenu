package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;

import java.io.File;

/**
 * <p>The PermissibleLoader interface defines methods for loading a {@link Permissible} from a configuration.</p>
 */
public interface PermissibleLoader {

    /**
     * Returns the key used to define the type of permissible.
     *
     * @return The key.
     */
    String getKey();

    /**
     * Creates a {@link Permissible} based on the provided configuration.
     *
     * @param path     The path in the configuration file.
     * @param accessor The map accessor that contains the configuration elements.
     * @param file     The file where the configuration is located.
     * @return The loaded {@link Permissible}.
     */
    Permissible load(String path, TypedMapAccessor accessor, File file);

}
