package fr.maxlego08.menu.zcore.utils.loader;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.maxlego08.menu.exceptions.InventoryException;

public interface Loader<T> {

	/**
	 * Load object from yml
	 *
	 * @param configuration
	 * @param path
	 * @return element
	 */
	T load(YamlConfiguration configuration, String path, Object... objects) throws InventoryException;

	/**
	 * Save object to yml
	 *
	 * @param object
	 * @param configuration
	 * @param path
	 */
	void save(T object, YamlConfiguration configuration, String path, Object... objects);

}
