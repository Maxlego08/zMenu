package fr.maxlego08.menu.api.loader;

import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;

public interface ButtonLoader {

	/**
	 * Returns the class that will be used for the button
	 * 
	 * @return classz
	 */
	public Class<? extends Button> getButton();

	/**
	 * Return the plugin where the loader button comes from
	 * 
	 * @return plugin
	 */
	public Plugin getPlugin();
	
}
