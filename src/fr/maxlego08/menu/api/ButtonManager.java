package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.plugin.Plugin;

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

    void registerPermissible(String key, Class<? extends Permissible> pClass);

    Map<String, Class<? extends Permissible>> getPermissibles();

    Optional<Class<? extends Permissible>> getPermission(String key);

    List<Permissible> loadPermissible(List<Map<String, Object>> elements, String path);

}
