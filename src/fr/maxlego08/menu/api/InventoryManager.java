package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.storage.Savable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * <p>Inventories management:</p>
 * <ul>
 *     <li>Loading</li>
 *     <li>Delete</li>
 *     <li>Opening</li>
 *     <li>Etc...</li>
 * </ul>
 * <p>Api example here: <a href="https://docs.zmenu.dev/api/create-inventory">https://docs.zmenu.dev/api/create-inventory</a></p>
 */
public interface InventoryManager extends Savable, Listener {

    /**
     * Allows to load an inventory, the plugin will retrieve the resource of
     * your plugin to save it if it does not exist.
     *
     * @param plugin   The plugin that will load the inventory
     * @param fileName Name of the file where the inventory is located
     * @return New {@link Inventory}
     * @throws InventoryException Error loading inventory
     */
    Inventory loadInventoryOrSaveResource(Plugin plugin, String fileName) throws InventoryException;


    /**
     * Allows to load an inventory, the plugin will retrieve the resource of
     * your plugin to save it if it does not exist. You must add the class of
     * your plugin as a parameter. More information about custom class <a href="https://docs.zmenu.dev/api/create-inventory#load-custom-inventory">here</a>
     *
     * @param plugin   The plugin that will load the inventory
     * @param fileName Name of the file where the inventory is located
     * @param classz   Class that will be used for inventory, by default it's will be ZInventory
     * @return New {@link Inventory}
     * @throws InventoryException Error loading inventory
     */
    Inventory loadInventoryOrSaveResource(Plugin plugin, String fileName, Class<? extends Inventory> classz) throws InventoryException;

    /**
     * Allows you to load an inventory from a file. You must add the class of
     * your plugin as a parameter. More information about custom class <a href="https://docs.zmenu.dev/api/create-inventory#load-custom-inventory">here</a>
     *
     * @param plugin   The plugin that will load the inventory
     * @param fileName Name of the file where the inventory is located
     * @param classz   Class that will be used for inventory, by default it's will be ZInventory
     * @return New {@link Inventory}
     * @throws InventoryException Error loading inventory
     */
    Inventory loadInventory(Plugin plugin, String fileName, Class<? extends Inventory> classz) throws InventoryException;

    /**
     * Allows you to load an inventory from a file. You must add the class of
     * your plugin as a parameter. More information about custom class <a href="https://docs.zmenu.dev/api/create-inventory#load-custom-inventory">here</a>
     *
     * @param plugin The plugin that will load the inventory
     * @param file   file where the inventory is located
     * @param classz - Class that will be used for inventory, by default it's will be ZInventory
     * @return New {@link Inventory}
     * @throws InventoryException Error loading inventory
     */
    Inventory loadInventory(Plugin plugin, File file, Class<? extends Inventory> classz) throws InventoryException;

    /**
     * Allows you to load an inventory from a file You must add the class of
     * your plugin as a parameter
     *
     * @param plugin   The plugin that will load the inventory
     * @param fileName Name of the file where the inventory is located
     * @return New {@link Inventory}
     * @throws InventoryException Error loading inventory
     */
    Inventory loadInventory(Plugin plugin, String fileName) throws InventoryException;

    /**
     * Allows you to load an inventory from a file You must add the class of
     * your plugin as a parameter
     *
     * @param plugin The plugin that will load the inventory
     * @param file   file where the inventory is located
     * @return New {@link Inventory}
     * @throws InventoryException Error loading inventory
     */
    Inventory loadInventory(Plugin plugin, File file) throws InventoryException;

    /**
     * Allows you to return an inventory according to its name
     *
     * @param name Inventory name
     * @return optional of {@link Inventory}
     */
    Optional<Inventory> getInventory(String name);

    /**
     * Allows you to return an inventory according to its name and the plugin
     *
     * @param plugin The plugin of where was generated the inventory
     * @param name   Inventory name
     * @return optional of {@link Inventory}
     */
    Optional<Inventory> getInventory(Plugin plugin, String name);

    /**
     * Allows you to return an inventory according to its name and the plugin
     * name
     *
     * @param pluginName The plugin name of where was generated the inventory
     * @param name       Inventory name
     * @return optional of {@link Inventory}
     */
    Optional<Inventory> getInventory(String pluginName, String name);

    /**
     * Allows you to return the list of inventories
     *
     * @return collection of {@link Inventory}
     */
    Collection<Inventory> getInventories();

    /**
     * Allows you to return the list of inventories from a plugin
     *
     * @return inventories
     */
    Collection<Inventory> getInventories(Plugin plugin);

    /**
     * Allows you to delete an inventory
     *
     * @param inventory {@link Inventory} who will be deleted
     */
    void deleteInventory(Inventory inventory);

    /**
     * Allows you to delete an inventory, returns true if the inventory has been
     * deleted
     *
     * @param name Inventory name
     * @return boolean
     */
    boolean deleteInventory(String name);

    /**
     * Allows you to delete the list of inventories of a plugin
     *
     * @param plugin The plugin
     */
    void deleteInventories(Plugin plugin);

    /**
     * Open an inventory to a player
     *
     * @param player    Player to whom the inventory must be opened
     * @param inventory The inventory to be opened
     */
    void openInventory(Player player, Inventory inventory);

    /**
     * Open an inventory to a player
     *
     * @param player    Player to whom the inventory must be opened
     * @param inventory The inventory to be opened
     * @param page      inventory page
     */
    void openInventory(Player player, Inventory inventory, int page);

    /**
     * Open an inventory to a player
     *
     * @param player         Player to whom the inventory must be opened
     * @param inventory      The inventory to be opened
     * @param page           inventory page
     * @param oldInventories List of old inventories
     */
    void openInventory(Player player, Inventory inventory, int page, List<Inventory> oldInventories);

    /**
     * Open an inventory to a player
     *
     * @param player      Player to whom the inventory must be opened
     * @param inventory   The inventory to be opened
     * @param page        inventory page
     * @param inventories List of old inventories
     */
    void openInventory(Player player, Inventory inventory, int page, Inventory... inventories);

    /**
     * Allows to load the buttons The {@link ButtonLoadEvent} event will be
     * called, so you can add your own buttons using this event
     */
    void loadButtons();

    /**
     * Allows to load the inventories of the plugin
     */
    void loadInventories();

    /**
     * Allows you to register a material loader
     * More information <a href="https://docs.zmenu.dev/api/create-material-loader">here</a>
     *
     * @param materialLoader New material loader
     * @return boolean True if registered
     */
    boolean registerMaterialLoader(MaterialLoader materialLoader);

    /**
     * Returns a material loader based on a key
     *
     * @param key The key to identify the material loader
     * @return optional of {@link MaterialLoader}
     */
    Optional<MaterialLoader> getMaterialLoader(String key);

    /**
     * Return the list of material loader
     *
     * @return materials List of {@link MaterialLoader}
     */
    Collection<MaterialLoader> getMaterialLoader();

    /**
     * Allows to open an inventory according to the name and the plugin
     *
     * @param player        Player who will open the inventory
     * @param plugin        The plugin where the inventory comes from
     * @param inventoryName Name of the inventory to be opened
     */
    void openInventory(Player player, Plugin plugin, String inventoryName);

    /**
     * Allows to open an inventory according to the name and the plugin name
     *
     * @param player        Player who will open the inventory
     * @param pluginName    The plugin where the inventory comes from
     * @param inventoryName Name of the inventory to be opened
     */
    void openInventory(Player player, String pluginName, String inventoryName);

    /**
     * Allows you to open an inventory according to the name Attention, here the
     * plugin will search in all inventories, it is more appropriate to use the
     * method {@link #openInventory(Player, Plugin, String)}
     *
     * @param player        Player who will open the inventory
     * @param inventoryName Name of the inventory to be opened
     */
    void openInventory(Player player, String inventoryName);

    /**
     * Returns a plugin optional based on its name.
     *
     * @param pluginName The name of the plugin to be found
     * @return optional An optional that can contain the plugin if it exists.
     */
    Optional<Plugin> getPluginIgnoreCase(String pluginName);

    /**
     * Allows you to reload the configuration of an inventory
     *
     * @param inventory The inventory that needs to be reloaded
     */
    void reloadInventory(Inventory inventory);

    /**
     * Set item name and item meta using spigot or adventure api
     *
     * @return MetaUpdater
     */
    MetaUpdater getMeta();

    /**
     * Get current open inventory
     *
     * @param player inventory
     * @return optional of inventory
     */
    Optional<Inventory> getCurrentPlayerInventory(Player player);

}
