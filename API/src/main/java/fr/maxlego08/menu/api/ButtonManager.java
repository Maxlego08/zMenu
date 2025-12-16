package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code ButtonManager} interface offers a centralized management system for buttons, enabling the creation,
 * registration, and retrieval of different types of buttons via designated loaders.
 *
 * <p>For creating a {@link Button}, utilize a {@link ButtonLoader} and register it with the {@code ButtonManager}.</p>
 * <p>This interface allows plugins to manage buttons, facilitating the dynamic creation and interaction of menus.</p>
 *
 * <p>Documentation can be found here: <a href="https://docs.zmenu.dev/api/create-button#3.-register-button-1">ButtonManager Documentation</a></p>
 */
public interface ButtonManager {

    /**
     * Registers a new {@link ButtonLoader} responsible for button creation.
     *
     * @param button The {@link ButtonLoader} instance to register.
     */
    void register(ButtonLoader button);

    /**
     * Unregisters an existing {@link ButtonLoader}, ceasing its button creation responsibilities.
     *
     * @param button The {@link ButtonLoader} instance to unregister.
     */
    void unregister(ButtonLoader button);

    /**
     * Unregisters all {@link ButtonLoader} instances linked to a specific plugin.
     *
     * @param plugin The plugin whose loaders should be unregistered.
     */
    void unregisters(Plugin plugin);

    /**
     * Retrieves all registered {@link ButtonLoader} instances.
     *
     * @return A collection containing all registered {@link ButtonLoader} instances.
     */
    Collection<ButtonLoader> getLoaders();

    /**
     * Retrieves {@link ButtonLoader} instances associated with a given plugin.
     *
     * @param plugin The plugin whose loaders are to be retrieved.
     * @return A collection of {@link ButtonLoader} instances or an empty collection if none are found.
     */
    Collection<ButtonLoader> getLoaders(Plugin plugin);

    /**
     * Retrieves a {@link ButtonLoader} based on the name of the associated {@link Button}.
     *
     * @param name The name of the {@link ButtonLoader}.
     * @return An {@link Optional} containing the {@link ButtonLoader}, if found.
     */
    Optional<ButtonLoader> getLoader(String name);

    /**
     * Registers a new {@link PermissibleLoader} responsible for permissible creation.
     *
     * @param permissibleLoader The {@link PermissibleLoader} instance to register.
     */
    void registerPermissible(PermissibleLoader permissibleLoader);

    /**
     * Retrieves a mapping of permissible keys to their associated {@link PermissibleLoader} instances.
     *
     * @return A map linking permissible keys to their corresponding {@link PermissibleLoader} instances.
     */
    Map<String, PermissibleLoader> getPermissibles();

    /**
     * Retrieves an {@link Optional} {@link PermissibleLoader} based on a specific permissible key.
     *
     * @param key The key identifying the permissible.
     * @return An {@link Optional} containing the {@link PermissibleLoader}, if found.
     */
    Optional<PermissibleLoader> getPermission(String key);

    /**
     * Registers a new {@link ActionLoader} responsible for action creation.
     *
     * @param actionLoader The {@link ActionLoader} instance to register.
     */
    void registerAction(ActionLoader actionLoader);

    /**
     * Retrieves an {@link Optional} {@link ActionLoader} based on a specific action key.
     *
     * @param key The key identifying the action.
     * @return An {@link Optional} containing the {@link ActionLoader}, if found.
     */
    Optional<ActionLoader> getActionLoader(String key);

    /**
     * Converts a list of map elements from a configuration file into a list of {@link Permissible} objects.
     *
     * @param elements The list of configuration items detailing a permissible's entire configuration.
     * @param path     The path specifying the location of the permissible list.
     * @param file     The configuration file in use.
     * @return A list of {@link Permissible} objects derived from the configuration.
     */
    List<Permissible> loadPermissible(List<Map<String, Object>> elements, String path, File file);

    /**
     * Converts a list of map elements from a configuration file into a list of {@link Permissible} objects.
     *
     * @param configuration The configuration file containing the permissible list.
     * @param path          The path specifying the location of the permissible list.
     * @param file          The file where the configuration is located.
     * @return A list of {@link Permissible} objects derived from the configuration.
     */
    List<Permissible> loadPermissible(YamlConfiguration configuration, String path, File file);

    /**
     * Converts a list of map elements from a configuration file into a list of {@link Action} objects.
     *
     * <p>Further action documentation is available at: <a href="https://docs.zmenu.dev/configurations/actions">Action Documentation</a></p>
     *
     * @param elements The list of configuration items detailing an action's entire configuration.
     * @param path     The path specifying the location of the action list.
     * @param file     The configuration file in use.
     * @return A list of {@link Action} objects derived from the configuration.
     */
    List<Action> loadActions(List<Map<String, Object>> elements, String path, File file);

    /**
     * Converts a list of map elements from a configuration file into a list of {@link Action} objects.
     *
     * <p>Further action documentation is available at: <a href="https://docs.zmenu.dev/configurations/actions">Action Documentation</a></p>
     *
     * @param configuration The configuration file containing the action list.
     * @param path          The path specifying the location of the action list.
     * @param file          The configuration file in use.
     * @return A list of {@link Action} objects derived from the configuration.
     */
    List<Action> loadActions(YamlConfiguration configuration, String path, File file);

    List<Requirement> loadRequirements(YamlConfiguration configuration, String path, File file) throws InventoryException;

    Requirement loadRequirement(YamlConfiguration configuration, String path, File file) throws InventoryException;

    /**
     * Retrieves a list of all empty actions from the given configuration elements.
     *
     * <p>This method is used to identify any actions specified in the configuration that do not have a corresponding
     * action loader. This is useful for printing out any errors that may have occurred during the loading process.</p>
     *
     * @param elements The list of configuration items detailing an action's entire configuration.
     * @return A list of all empty actions from the given configuration elements.
     */
    List<String> getEmptyActions(List<Map<String, Object>> elements);

    /**
     * Retrieves a list of all empty permissibles from the given configuration elements.
     *
     * <p>This method is used to identify any permissibles specified in the configuration that do not have a corresponding
     * permissible loader. This is useful for printing out any errors that may have occurred during the loading process.</p>
     *
     * @param elements The list of configuration items detailing a permissible's entire configuration.
     * @return A list of all empty permissibles from the given configuration elements.
     */
    List<String> getEmptyPermissible(List<Map<String, Object>> elements);

    /**
     * Retrieves a {@link Loader} for a {@link Button} object based on the given parameters.
     *
     * <p>This method is used to create a loader for a button object, given the plugin instance, file, size, and matrix.</p>
     *
     * @param menuPlugin The plugin instance associated with the button loader.
     * @param file The file from which the button configuration is loaded.
     * @param size The size of the button.
     * @param matrix The matrix containing the button's configuration data.
     * @return A {@link Loader} for a {@link Button} object based on the given parameters.
     */
    Loader<Button> getLoaderButton(MenuPlugin menuPlugin, File file, int size, Map<Character, List<Integer>> matrix);
}