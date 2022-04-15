package fr.maxlego08.menu.api;

import java.util.Collection;
import java.util.Optional;

import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ButtonLoader;

public interface ButtonManager {

	/**
	 * Allows to register a ButtonLoader
	 * 
	 * @param plugin
	 * @param button
	 */
	public void register(ButtonLoader button);
	
	/**
	 * Allows you to delete a ButtonLoader
	 * 
	 * @param button
	 */
	public void unregister(ButtonLoader button);
	
	/**
	 * Allows to delete all ButtonLoader of a plugin
	 * 
	 * @param plugin
	 */
	public void unregisters(Plugin plugin);
	
	/**
	 * Return the list of ButtonLoader
	 * 
	 * @return loaders
	 */
	public Collection<ButtonLoader> getLoaders();
	
	/**
	 * Return the list of ButtonLoader according to a plugin
	 * 
	 * @param plugin
	 * @return loaders
	 */
	public Collection<ButtonLoader> getLoaders(Plugin plugin);
	
	/**
	 * Returns a ButtonLoader based on a button type
	 * 
	 * @param classz
	 * @return optional
	 */
	public Optional<ButtonLoader> getLoader(Class<? extends Button> classz);
	
	/**
	 * Returns a ButtonLoader based on a button name
	 * 
	 * @param name
	 * @return optional
	 */
	public Optional<ButtonLoader> getLoader(String name);
	
}
