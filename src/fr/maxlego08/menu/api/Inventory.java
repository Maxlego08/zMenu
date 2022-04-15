package fr.maxlego08.menu.api;

import java.util.Collection;
import java.util.List;

import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PlaceholderButton;

public interface Inventory {

	/**
	 * Returns the size of the inventory
	 * 
	 * @return size
	 */
	public int size();

	/**
	 * Returns the name of the inventory
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns the name of the file
	 * 
	 * @return fileName
	 */
	public String getFileName();

	/**
	 * Return the list of buttons
	 * 
	 * @return buttons
	 */
	public Collection<Button> getButtons();

	/**
	 * Returns the list of buttons according to a type
	 * 
	 * @param button
	 *            type
	 * @return buttons list
	 */
	public <T extends Button> List<T> getButtons(Class<T> type);

	/**
	 * Returns the plugin where the inventory comes from
	 * 
	 * @return plugin
	 */
	public Plugin getPlugin();

	/**
	 * Returns the maximum number of pages
	 * 
	 * @param objects
	 * @return page
	 */
	public int getMaxPage(Object... objects);

	/**
	 * Allows you to sort the buttons according to a page
	 * 
	 * @param page
	 * @param objects
	 * @return buttons
	 */
	public List<PlaceholderButton> sortButtons(int page, Object... objects);

}
