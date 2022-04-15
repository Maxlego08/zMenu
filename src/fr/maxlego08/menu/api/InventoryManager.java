package fr.maxlego08.menu.api;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;

public interface InventoryManager extends Saveable {

	/**
	 * Allows you to load an inventory from a file You must add the class of
	 * your plugin as a parameter
	 * 
	 * @param plugin
	 * @param fileName
	 * @return inventory
	 * @throws InventoryException
	 */
	public Inventory loadInventory(Plugin plugin, String fileName) throws InventoryException;
	
	/**
	 * Allows you to load an inventory from a file You must add the class of
	 * your plugin as a parameter
	 * 
	 * @param plugin
	 * @param file
	 * @return inventory
	 * @throws InventoryException
	 */
	public Inventory loadInventory(Plugin plugin, File file) throws InventoryException;

	/**
	 * Allows you to return an inventory according to its name
	 * 
	 * @param name
	 * @return optional
	 */
	public Optional<Inventory> getInventory(String name);

	/**
	 * Allows you to return the list of inventories
	 * 
	 * @return inventories
	 */
	public Collection<Inventory> getInventories();

	/**
	 * Allows you to return the list of inventories from a plugin
	 * 
	 * @return inventories
	 */
	public Collection<Inventory> getInventories(Plugin plugin);

	/**
	 * Allows you to delete an inventory
	 * 
	 * @param inventory
	 */
	public void deleteInventory(Inventory inventory);

	/**
	 * Allows you to delete an inventory, returns true if the inventory has been
	 * deleted
	 * 
	 * @param name
	 *            Inventory name
	 * @return boolean
	 */
	public boolean deleteInventory(String name);

	/**
	 * Allows you to delete the list of inventories of a plugin
	 * 
	 * @param inventory
	 */
	public void deleteInventories(Plugin plugin);

	/**
	 * Open a inventory to a player
	 * 
	 * @param player
	 * @param inventory
	 */
	public void openInventory(Player player, Inventory inventory);

	/**
	 * Open a inventory to a player
	 * 
	 * @param player
	 * @param inventory
	 * @param page
	 */
	public void openInventory(Player player, Inventory inventory, int page);

	/**
	 * Open a inventory to a player
	 * 
	 * @param player
	 * @param inventory
	 * @param page
	 * @param oldInventories
	 */
	public void openInventory(Player player, Inventory inventory, int page, List<Inventory> oldInventories);

}
