package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;

import java.io.File;
import java.util.List;

/**
 * A loader for creating instances of {@link Action} based on configuration.
 */
public abstract class ActionLoader {

    private final List<String> keys;

    public ActionLoader(List<String> keys) {
        this.keys = keys;
    }

    public ActionLoader(String... keys) {
        this.keys = List.of(keys);
    }

    /**
     * Gets the key's that defines the type of action.
     *
     * @return keys.
     */
    public List<String> getKeys() {
        return this.keys;
    }

    /**
     * Creates an instance of {@link Action} based on the provided configuration.
     *
     * @param path     The path in the configuration file.
     * @param accessor The map accessor containing the configuration elements.
     * @param file     The file where the configuration is located.
     * @return The created {@link Action}.
     */
    public abstract Action load(String path, TypedMapAccessor accessor, File file);
}
