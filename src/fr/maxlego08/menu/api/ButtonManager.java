package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The `ButtonManager` interface provides a centralized management system for buttons, allowing the creation,
 * registration, and retrieval of various types of buttons through designated loaders.
 *
 * Documentation: <a href="https://docs.zmenu.dev/api/create-button#3.-register-button-1">ButtonManager Documentation</a>
 *
 * <p>To create a {@link Button}, use a {@link ButtonLoader}, and then register it with the `ButtonManager`.</p>
 * <p>Buttons can be managed by plugins using this interface, facilitating dynamic menu creation and interaction.</p>
 */
public interface ButtonManager {

    /**
     * Registers a new {@link ButtonLoader} for handling button creation.
     *
     * @param button The new {@link ButtonLoader} to register.
     */
    void register(ButtonLoader button);

    /**
     * Unregisters a {@link ButtonLoader} to stop handling button creation.
     *
     * @param button The {@link ButtonLoader} to unregister.
     */
    void unregister(ButtonLoader button);

    /**
     * Unregisters all {@link ButtonLoader}s associated with a specific plugin.
     *
     * @param plugin The plugin for which to unregister all loaders.
     */
    void unregisters(Plugin plugin);

    /**
     * Retrieves a collection of all registered {@link ButtonLoader}s.
     *
     * @return A collection of {@link ButtonLoader}s.
     */
    Collection<ButtonLoader> getLoaders();

    /**
     * Retrieves a collection of {@link ButtonLoader}s associated with a specific plugin.
     *
     * @param plugin The plugin for which to retrieve loaders.
     * @return A collection of {@link ButtonLoader}s or an empty collection if none are found.
     */
    Collection<ButtonLoader> getLoaders(Plugin plugin);

    /**
     * Retrieves a {@link ButtonLoader} based on the class of the associated {@link Button}.
     *
     * @param classz Class of {@link Button}.
     * @return An optional {@link ButtonLoader}.
     */
    Optional<ButtonLoader> getLoader(Class<? extends Button> classz);

    /**
     * Retrieves a {@link ButtonLoader} based on the name of the associated {@link Button}.
     *
     * @param name The name of the {@link ButtonLoader}.
     * @return An optional {@link ButtonLoader}.
     */
    Optional<ButtonLoader> getLoader(String name);

    /**
     * Registers a new {@link PermissibleLoader} for handling permissible creation.
     *
     * @param permissibleLoader The new {@link PermissibleLoader} to register.
     */
    void registerPermissible(PermissibleLoader permissibleLoader);

    /**
     * Retrieves a map of permissible keys and associated {@link PermissibleLoader}s.
     *
     * @return A map of permissible keys to {@link PermissibleLoader}s.
     */
    Map<String, PermissibleLoader> getPermissibles();

    /**
     * Retrieves an optional {@link PermissibleLoader} based on a permissible key.
     *
     * @param key The key associated with the permissible.
     * @return An optional {@link PermissibleLoader}.
     */
    Optional<PermissibleLoader> getPermission(String key);

    /**
     * Registers a new {@link ActionLoader} for handling action creation.
     *
     * @param actionLoader The new {@link ActionLoader} to register.
     */
    void registerAction(ActionLoader actionLoader);

    /**
     * Retrieves an optional {@link ActionLoader} based on an action key.
     *
     * @param key The key associated with the action.
     * @return An optional {@link ActionLoader}.
     */
    Optional<ActionLoader> getActionLoader(String key);

    /**
     * Transforms a list of map elements from a configuration file into a list of permissibles.
     *
     * @param elements List of items from the configuration containing the entire configuration of a permissible.
     * @param path     The path to or from the list of permissibles.
     * @param file     The current configuration file.
     * @return A list of {@link Permissible}s.
     */
    List<Permissible> loadPermissible(List<Map<String, Object>> elements, String path, File file);

    /**
     * Transforms a list of map elements from a configuration file into a list of actions.
     *
     * Documentation about actions: <a href="https://docs.zmenu.dev/configurations/actions">Action Documentation</a>
     *
     * @param elements List of items from the configuration containing the entire configuration of an action.
     * @param path     The path to or from the list of actions.
     * @param file     The current configuration file.
     * @return A list of {@link Action}s.
     */
    List<Action> loadActions(List<Map<String, Object>> elements, String path, File file);
}
