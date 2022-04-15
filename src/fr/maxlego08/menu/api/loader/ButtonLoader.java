package fr.maxlego08.menu.api.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.PlaceholderAction;

public interface ButtonLoader {

	/**
	 * Returns the class that will be used for the button
	 * 
	 * @return classz
	 */
	public Class<? extends Button> getButton();

	/**
	 * Returns the name of the button
	 * 
	 * @return name
	 */
	public String getName();
	
	/**
	 * Return the plugin where the loader button comes from
	 * 
	 * @return plugin
	 */
	public Plugin getPlugin();
	
	/**
	 * Allows you to load a button
	 * 
	 * @param buttonName
	 * @param itemStack
	 * @param slot
	 * @param isPermanent
	 * @param permission
	 * @param elseButton
	 * @param action
	 * @param placeholder
	 * @param value
	 */
	public Button load(YamlConfiguration configuration, String path, String buttonName, ItemStack itemStack, int slot, boolean isPermanent, String permission,
			Button elseButton, PlaceholderAction action, String placeholder, String value);
	
}
