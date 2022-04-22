package fr.maxlego08.menu.api.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public interface MaterialLoader {

	/**
	 * Return the key to load the itemstack
	 * 
	 * @return key
	 */
	public String getKey();
	
	/**
	 * Allows to load an itemstack according to a plugin
	 * 
	 * @param configuration
	 * @param path
	 * @param materialString
	 * @return itemstack
	 */
	public ItemStack load(YamlConfiguration configuration, String path, String materialString);
	
}
