package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.storage.Savable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * <p>Inventory Management:</p>
 * <ul>
 *     <li>Loading</li>
 *     <li>Deletion</li>
 *     <li>Opening</li>
 *     <li>etc...</li>
 * </ul>
 * <p>API example here: <a href="https://docs.zmenu.dev/api/create-inventory">https://docs.zmenu.dev/api/create-inventory</a></p>
 */
public interface InventoryManager extends Savable, Listener {

    /**
     * Loads an inventory. The plugin will retrieve the resource of
     * your plugin to save it if it does not exist.
     *
     * @param plugin   The plugin loading the inventory.
     * @param fileName Name of the file where the inventory is located.
     * @return New {@link Inventory}.
     * @throws InventoryException Error loading inventory.
     */
    Inventory loadInventoryOrSaveResource(Plugin plugin, String fileName) throws InventoryException;

    /**
     * Loads an inventory. The plugin will retrieve the resource of
     * your plugin to save it if it does not exist. You must add the class of
     * your plugin as a parameter. More information about custom class <a href="https://docs.zmenu.dev/api/create-inventory#load-custom-inventory">here</a>
     *
     * @param plugin   The plugin loading the inventory.
     * @param fileName Name of the file where the inventory is located.
     * @param classz   Class that will be used for the inventory; by default, it will be ZInventory.
     * @return New {@link Inventory}.
     * @throws InventoryException Error loading inventory.
     */
    Inventory loadInventoryOrSaveResource(Plugin plugin, String fileName, Class<? extends Inventory> classz) throws InventoryException;

    /**
     * Loads an inventory from a file. You must add the class of
     * your plugin as a parameter. More information about custom class <a href="https://docs.zmenu.dev/api/create-inventory#load-custom-inventory">here</a>
     *
     * @param plugin   The plugin loading the inventory.
     * @param fileName Name of the file where the inventory is located.
     * @param classz   Class that will be used for the inventory; by default, it will be ZInventory.
     * @return New {@link Inventory}.
     * @throws InventoryException Error loading inventory.
     */
    Inventory loadInventory(Plugin plugin, String fileName, Class<? extends Inventory> classz) throws InventoryException;

    /**
     * Loads an inventory from a file. You must add the class of
     * your plugin as a parameter.
     *
     * @param plugin The plugin loading the inventory.
     * @param file   File where the inventory is located.
     * @param classz Class that will be used for the inventory; by default, it will be ZInventory.
     * @return New {@link Inventory}.
     * @throws InventoryException Error loading inventory.
     */
    Inventory loadInventory(Plugin plugin, File file, Class<? extends Inventory> classz) throws InventoryException;

    /**
     * Loads an inventory from a file. You must add the class of
     * your plugin as a parameter.
     *
     * @param plugin   The plugin loading the inventory.
     * @param fileName Name of the file where the inventory is located.
     * @return New {@link Inventory}.
     * @throws InventoryException Error loading inventory.
     */
    Inventory loadInventory(Plugin plugin, String fileName) throws InventoryException;

    /**
     * Loads an inventory from a file. You must add the class of
     * your plugin as a parameter.
     *
     * @param plugin The plugin loading the inventory.
     * @param file   File where the inventory is located.
     * @return New {@link Inventory}.
     * @throws InventoryException Error loading inventory.
     */
    Inventory loadInventory(Plugin plugin, File file) throws InventoryException;

    /**
     * Returns an optional of {@link Inventory} based on its name.
     *
     * @param name Inventory name.
     * @return Optional of {@link Inventory}.
     */
    Optional<Inventory> getInventory(String name);

    /**
     * Returns an optional of {@link Inventory} based on its name and the plugin.
     *
     * @param plugin The plugin where the inventory comes from.
     * @param name   Inventory name.
     * @return Optional of {@link Inventory}.
     */
    Optional<Inventory> getInventory(Plugin plugin, String name);

    /**
     * Returns an optional of {@link Inventory} based on its name and the plugin name.
     *
     * @param pluginName The plugin name where the inventory comes from.
     * @param name       Inventory name.
     * @return Optional of {@link Inventory}.
     */
    Optional<Inventory> getInventory(String pluginName, String name);

    /**
     * Returns a collection of all loaded inventories.
     *
     * @return Collection of {@link Inventory}.
     */
    Collection<Inventory> getInventories();

    /**
     * Returns a collection of inventories loaded from a specific plugin.
     *
     * @param plugin The plugin.
     * @return Inventories.
     */
    Collection<Inventory> getInventories(Plugin plugin);

    /**
     * Deletes an inventory.
     *
     * @param inventory {@link Inventory} to be deleted.
     */
    void deleteInventory(Inventory inventory);

    /**
     * Deletes an inventory based on its name.
     *
     * @param name Inventory name.
     * @return True if the inventory has been deleted.
     */
    boolean deleteInventory(String name);

    /**
     * Deletes all inventories loaded from a plugin.
     *
     * @param plugin The plugin.
     */
    void deleteInventories(Plugin plugin);

    /**
     * Opens an inventory for a player.
     *
     * @param player    Player to whom the inventory must be opened.
     * @param inventory The inventory to be opened.
     */
    void openInventory(Player player, Inventory inventory);

    /**
     * Opens an inventory for a player on a specific page.
     *
     * @param player    Player to whom the inventory must be opened.
     * @param inventory The inventory to be opened.
     * @param page      Inventory page.
     */
    void openInventory(Player player, Inventory inventory, int page);

    /**
     * Opens an inventory for a player on a specific page with old inventories.
     *
     * @param player         Player to whom the inventory must be opened.
     * @param inventory      The inventory to be opened.
     * @param page           Inventory page.
     * @param oldInventories List of old inventories.
     */
    void openInventory(Player player, Inventory inventory, int page, List<Inventory> oldInventories);

    /**
     * Opens an inventory for a player on a specific page with old inventories.
     *
     * @param player      Player to whom the inventory must be opened.
     * @param inventory   The inventory to be opened.
     * @param page        Inventory page.
     * @param inventories List of old inventories.
     */
    void openInventory(Player player, Inventory inventory, int page, Inventory... inventories);

    /**
     * Loads buttons. The {@link ButtonLoaderRegisterEvent} event will be
     * called, allowing you to add your own buttons using this event.
     */
    void loadButtons();

    /**
     * Loads inventories of the plugin.
     */
    void loadInventories();

    /**
     * Registers a material loader.
     *
     * @param materialLoader New material loader.
     * @return True if registered.
     */
    boolean registerMaterialLoader(MaterialLoader materialLoader);

    /**
     * Returns a material loader based on a key.
     *
     * @param key The key to identify the material loader.
     * @return Optional of {@link MaterialLoader}.
     */
    Optional<MaterialLoader> getMaterialLoader(String key);

    /**
     * Returns a list of material loaders.
     *
     * @return List of {@link MaterialLoader}.
     */
    Collection<MaterialLoader> getMaterialLoader();

    /**
     * Opens an inventory based on its name and the plugin.
     *
     * @param player        Player who will open the inventory.
     * @param plugin        The plugin where the inventory comes from.
     * @param inventoryName Name of the inventory to be opened.
     */
    void openInventory(Player player, Plugin plugin, String inventoryName);

    /**
     * Opens an inventory based on its name and the plugin name.
     *
     * @param player        Player who will open the inventory.
     * @param pluginName    The plugin where the inventory comes from.
     * @param inventoryName Name of the inventory to be opened.
     */
    void openInventory(Player player, String pluginName, String inventoryName);

    /**
     * Opens an inventory based on its name. Note that
     * this method searches in all inventories; it is more appropriate to use
     * the method {@link #openInventory(Player, Plugin, String)}.
     *
     * @param player        Player who will open the inventory.
     * @param inventoryName Name of the inventory to be opened.
     */
    void openInventory(Player player, String inventoryName);

    /**
     * Returns an optional of a plugin based on its name.
     *
     * @param pluginName The name of the plugin to be found.
     * @return Optional of a plugin that can contain the plugin if it exists.
     */
    Optional<Plugin> getPluginIgnoreCase(String pluginName);

    /**
     * Reloads the configuration of an inventory.
     *
     * @param inventory The inventory that needs to be reloaded.
     */
    void reloadInventory(Inventory inventory);

    /**
     * Sets item name and item meta using Spigot or Adventure API.
     *
     * @return MetaUpdater.
     */
    MetaUpdater getMeta();

    /**
     * Gets the current open inventory of a player.
     *
     * @param player Player whose inventory is checked.
     * @return Optional of inventory.
     */
    Optional<Inventory> getCurrentPlayerInventory(Player player);

    /**
     * Unregisters a FastEvent listener.
     *
     * @param plugin The plugin.
     */
    void unregisterListener(Plugin plugin);

    /**
     * Adds a FastEvent listener, which is faster than Bukkit events.
     *
     * @param plugin    The plugin.
     * @param fastEvent The fastEvent.
     */
    void registerFastEvent(Plugin plugin, FastEvent fastEvent);

    /**
     * Gets the FastEvents.
     *
     * @return Listeners.
     */
    Collection<FastEvent> getFastEvents();

    /**
     * Displays the list of available inventories.
     *
     * @param sender Command sender.
     */
    void sendInventories(CommandSender sender);

    /**
     * Creates a new inventory file.
     *
     * @param sender       Command sender.
     * @param fileName     Inventory file name.
     * @param inventorySize Inventory size.
     * @param inventoryName Inventory name.
     */
    void createNewInventory(CommandSender sender, String fileName, int inventorySize, String inventoryName);

    /**
     * Opens the player’s current inventory again.
     *
     * @param player The player.
     */
    void updateInventory(Player player);
}
