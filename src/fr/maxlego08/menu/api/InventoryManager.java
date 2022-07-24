package fr.maxlego08.menu.api;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.storage.Saveable;

public interface InventoryManager extends Saveable {

	/**
	 * Allows you to load an inventory from a file You must add the class of
	 * your plugin as a parameter
	 * 
	 * @param plugin
	 * @param fileName
	 * @param classz Default class ZInventory
	 * @return inventory
	 * @throws InventoryException
	 */
	public Inventory loadInventory(Plugin plugin, String fileName, Class<? extends Inventory> classz)
			throws InventoryException;

	/**
	 * Allows you to load an inventory from a file You must add the class of
	 * your plugin as a parameter
	 * 
	 * @param plugin
	 * @param file
	 * @param classz
	 *            - Default class ZInventory
	 * @return inventory
	 * @throws InventoryException
	 */
	public Inventory loadInventory(Plugin plugin, File file, Class<? extends Inventory> classz)
			throws InventoryException;

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
	 * @param fileF
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
	 * Allows you to return an inventory according to its name and the plugin
	 * 
	 * @param plugin
	 * @param name
	 * @return optional
	 */
	public Optional<Inventory> getInventory(Plugin plugin, String name);

	/**
	 * Allows you to return an inventory according to its name and the plugin
	 * name
	 * 
	 * @param pluginName
	 * @param name
	 * @return optional
	 */
	public Optional<Inventory> getInventory(String pluginName, String name);

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

	/**
	 * Open a inventory to a player
	 * 
	 * @param player
	 * @param inventory
	 * @param page
	 * @param inventories
	 */
	public void openInventory(Player player, Inventory inventory, int page, Inventory... inventories);

	/**
	 * Allows to load the buttons The {@link ButtonLoadEvent} event will be
	 * called, so you can add your own buttons using this event
	 */
	public void loadButtons();

	/**
	 * Allows to load the inventories of the plugin
	 */
	public void loadInventories();

	/**
	 * Allows you to register a material loader
	 * 
	 * @param materialLoader
	 * @return boolean True if registered
	 */
	public boolean registerMaterialLoader(MaterialLoader materialLoader);

	/**
	 * Returns a material loader based on a key
	 * 
	 * @param key
	 * @return optional
	 */
	public Optional<MaterialLoader> getMaterialLoader(String key);

	/**
	 * Return the list of material loader
	 * 
	 * @return materials List of material loader
	 */
	public Collection<MaterialLoader> getMaterialLoader();

	/**
	 * Allows to open an inventory according to the name and the plugin
	 * 
	 * @param player Player who will open the inventory
	 * @param plugin The plugin where the inventory comes from
	 * @param inventoryName Name of the inventory to be opened
	 */
	public void openInventory(Player player, Plugin plugin, String inventoryName);

	/**
	 * Allows to open an inventory according to the name and the plugin name
	 * 
	 * @param player Player who will open the inventory
	 * @param pluginName The plugin where the inventory comes from
	 * @param inventoryName Name of the inventory to be opened
	 */
	public void openInventory(Player player, String pluginName, String inventoryName);

	/**
	 * Allows you to open an inventory according to the name
	 * Attention, here the plugin will search in all inventories, it is more appropriate to use the method {@link #openInventory(Player, Plugin, String)}
	 * 
	 * @param player Player who will open the inventory
	 * @param inventoryName Name of the inventory to be opened 
	 */
	public void openInventory(Player player, String inventoryName);

	/**
	 * Returns a plugin optional based on its name. 
	 * 
	 * @param pluginName The name of the plugin to be found
	 * @return optional An optional that can contain the plugin if it exists.
	 */
	public Optional<Plugin> getPluginIgnoreCase(String pluginName);

	/**
	 * Allows you to reload the configuration of an inventory
	 * 
	 * @param inventory The inventory that needs to be reloaded
	 */
	public void reloadInventory(Inventory inventory);

}
