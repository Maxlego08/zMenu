package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class PermissibleLoader {

    private final String key;

    public PermissibleLoader(String key) {
        this.key = key;
    }

    /**
     * Returns the key used to define the type of permissible.
     *
     * @return The key.
     */
    public String getKey(){
        return this.key;
    }

    /**
     * Creates a {@link Permissible} based on the provided configuration.
     *
     * @param path     The path in the configuration file.
     * @param accessor The map accessor that contains the configuration elements.
     * @param file     The file where the configuration is located.
     * @return The loaded {@link Permissible}.
     */
    public abstract Permissible load(String path, TypedMapAccessor accessor, File file);

    /**
     * Loads a list of Action from a configuration node.
     * The actions are loaded from the list of maps in the configuration node.
     *
     * @param buttonManager the button manager used to load the actions.
     * @param accessor      the typed map accessor to access the configuration node.
     * @param key           the key of the configuration node to read.
     * @param path          the path of the configuration file to read.
     * @param file          the file of the configuration file to read.
     * @return the list of loaded actions.
     */
    @SuppressWarnings("unchecked")
    protected List<Action> loadAction(ButtonManager buttonManager, TypedMapAccessor accessor, String key, String path, File file) {
        return buttonManager.loadActions((List<Map<String, Object>>) accessor.getObject(key, new ArrayList<Map<String, Object>>()), path, file);
    }

    /**
     * Load a list of Permissible from a configuration node.
     *
     * @param buttonManager the button manager used to load the actions.
     * @param accessor      the typed map accessor to access the configuration.
     * @param key           the key of the list of permissible.
     * @param path          the path of the file.
     * @param file          the file of the configuration.
     * @return a list of Permissible.
     */
    @SuppressWarnings("unchecked")
    protected List<Permissible> loadPermissible(ButtonManager buttonManager, TypedMapAccessor accessor, String key, String path, File file) {
        return buttonManager.loadPermissible((List<Map<String, Object>>) accessor.getObject(key, new ArrayList<Map<String, Object>>()), path, file);
    }

}
