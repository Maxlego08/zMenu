package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Documentation: <a href="https://docs.zmenu.dev/api/create-button#3.-register-button-1">https://docs.zmenu.dev/api/create-button</a>
 *
 * <p>Managing buttons. To create a {@link Button} you must use a {@link ButtonLoader}.</p>
 * <p>You can then register your {@link ButtonLoader} and the plugin can use it</p>
 */
public interface ButtonManager {

    /**
     * Allows to register a ButtonLoader
     *
     * @param button The new {@link ButtonLoader}
     */
    void register(ButtonLoader button);

    /**
     * Allows you to delete a ButtonLoader
     *
     * @param button The {@link ButtonLoader}
     */
    void unregister(ButtonLoader button);

    /**
     * Allows to delete all ButtonLoader of a plugin
     *
     * @param plugin The plugin
     */
    void unregisters(Plugin plugin);

    /**
     * Return the list of ButtonLoader
     *
     * @return loaders
     */
    Collection<ButtonLoader> getLoaders();

    /**
     * Return the list of ButtonLoader according to a plugin
     *
     * @param plugin The The plugin
     * @return collections or {@link ButtonLoader}
     */
    Collection<ButtonLoader> getLoaders(Plugin plugin);

    /**
     * Returns a ButtonLoader based on a button type
     *
     * @param classz Class of {@link Button}
     * @return optional
     */
    Optional<ButtonLoader> getLoader(Class<? extends Button> classz);

    /**
     * Returns a ButtonLoader based on a button name
     *
     * @param name Button loader name
     * @return optional
     */
    Optional<ButtonLoader> getLoader(String name);

    /**
     *
     * @param permissibleLoader Loader
     */
    void registerPermissible(PermissibleLoader permissibleLoader);

    /**
     *
     * @return map of key and permissible class
     */
    Map<String, PermissibleLoader> getPermissibles();

    /**
     *
     * @param key permissible key
     * @return optional of permissible class
     */
    Optional<PermissibleLoader> getPermission(String key);

    /**
     * Register a new Action loader
     *
     * @param actionLoader {@link ActionLoader}
     */
    void registerAction(ActionLoader actionLoader);

    /**
     * Returns an optional {@link ActionLoader}, if the key does not exist then the optional will be empty
     *
     * @param key action key
     * @return optional of {@link ActionLoader}
     */
    Optional<ActionLoader> getActionLoader(String key);

    /**
     * Transform a map list, which comes from a configuration file into a permissible list<br>
     *
     * @param elements list of items that come from the configuration, will contain the entire configuration of a permissible
     * @param path the path to or from the list of permissibles
     * @return list of {@link Permissible}
     */
    List<Permissible> loadPermissible(List<Map<String, Object>> elements, String path, File file);

    /**
     * Transform a map list, which comes from a configuration file into an action list<br>
     * Documentation about actions: <a href=https://docs.zmenu.dev/configurations/actions>https://docs.zmenu.dev/configurations/actions</a>
     *
     * @param elements list of items that come from the configuration, will contain the entire configuration of an action
     * @param path     the path to or from the list of actions
     * @param file     the current file
     * @return list of actions
     */
    List<Action> loadActions(List<Map<String, Object>> elements, String path, File file);
}
