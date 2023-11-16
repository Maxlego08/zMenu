package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;

import java.io.File;

public interface PermissibleLoader {

    /**
     * The key that will define the type of action
     *
     * @return key
     */
    String getKey();

    /**
     * Allows you to create a permissible
     *
     * @param path     the path in the configuration file
     * @param accessor the map accessor that contains the configuration elements
     * @param file     the file where the configuration is located
     * @return Permissible
     */
    Permissible load(String path, TypedMapAccessor accessor, File file);

}
