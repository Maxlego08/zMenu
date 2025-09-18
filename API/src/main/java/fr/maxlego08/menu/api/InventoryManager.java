package fr.maxlego08.menu.api;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
public interface InventoryManager extends Listener {

    /**
     * Loads an inventory. The plugin will retrieve the resource of
     * your plugin to save it if it does not exist.
     *
     * @param plugin   The plugin loading the inventory.
     * @param fileName Name of the file where the inventory is located.
     * @return New {@link Inventory}. Null if the loading of the inventory has been disabled in the config.
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
     * @return New {@link Inventory}. Null if the loading of the inventory has been disabled in the config.
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
     * Opens an inventory for a player on a specific page with its old inventories.
     *
     * @param player    Player to whom the inventory must be opened.
     * @param inventory The inventory to be opened.
     * @param page      Inventory page.
     */
    void openInventoryWithOldInventories(Player player, Inventory inventory, int page);

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
     * Opens an inventory based on its name and the plugin name.
     *
     * @param player        Player who will open the inventory.
     * @param pluginName    The plugin where the inventory comes from.
     * @param inventoryName Name of the inventory to be opened.
     * @param page          The page number of the inventory to open.
     */
    void openInventory(Player player, String pluginName, String inventoryName, int page);

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
     * Opens an inventory based on its name and a specific page number.
     * This method allows a player to view a particular page of an inventory,
     * facilitating navigation through multi-page inventories.
     *
     * @param player        Player who will open the inventory.
     * @param inventoryName Name of the inventory to be opened.
     * @param page          The page number of the inventory to open.
     */
    void openInventory(Player player, String inventoryName, int page);

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
     * @param sender        Command sender.
     * @param fileName      Inventory file name.
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

    /**
     * Opens the player’s current inventory again if the plugin is the same.
     *
     * @param player The player.
     * @param plugin The plugin.
     */
    void updateInventory(Player player, Plugin plugin);

    /**
     * Opens the player's inventory with the Player's current page.
     *
     * @param player The player.
     */
    void updateInventoryCurrentPage(Player player);

    /**
     * Save item in config file
     *
     * @param sender    Command Sender
     * @param itemStack The itemStack
     * @param name      The item name
     * @param type
     */
    void saveItem(CommandSender sender, ItemStack itemStack, String name, String type);

    /**
     * Transforms a string list into a click list type.
     * If the list contains ANY or ALL then the ClickType present in the plugin configuration will be added to the list.
     *
     * @param loadClicks - Click type as string
     * @return Loaded click type
     */
    List<ClickType> loadClicks(List<String> loadClicks);

    /**
     * Registers an ItemStackSimilar instance for verifying ItemStacks.
     * This method allows for adding a new ItemStack verification strategy to the system.
     *
     * @param itemStackSimilar The ItemStackSimilar instance to be registered.
     */
    void registerItemStackVerification(ItemStackSimilar itemStackSimilar);

    /**
     * Retrieves an Optional ItemStackSimilar instance based on its name.
     * This method is used to get a specific ItemStack verification strategy by its unique name.
     * If no verification strategy is found with the given name, an empty Optional is returned.
     *
     * @param name The name of the ItemStack verification strategy to retrieve.
     * @return An Optional containing the ItemStackSimilar instance if found, or an empty Optional otherwise.
     */
    Optional<ItemStackSimilar> getItemStackVerification(String name);

    /**
     * Returns a collection of all registered ItemStackSimilar instances.
     * This method is used to get all the available ItemStack verification strategies currently registered.
     * The collection contains instances of ItemStackSimilar, each representing a different verification strategy.
     *
     * @return A Collection of ItemStackSimilar instances representing all registered verification strategies.
     */
    Collection<ItemStackSimilar> getItemStackVerifications();

    /**
     * Gets the scheduler associated with this button option.
     *
     * @return The ZScheduler associated with this button option.
     */
    PlatformScheduler getScheduler();

    /**
     * Registers a new button option class with the plugin.
     *
     * @param plugin       The plugin with which the button option is to be registered.
     * @param buttonOption The class of the button option to register.
     */
    void registerOption(Plugin plugin, Class<? extends ButtonOption> buttonOption);

    /**
     * Unregisters all button options associated with the given plugin.
     *
     * @param plugin The plugin whose button options are to be unregistered.
     */
    void unregisterOptions(Plugin plugin);

    /**
     * Retrieves a map of all registered button options grouped by plugin.
     *
     * @return A Map where each key is a Plugin and each value is a List of button option classes associated with that plugin.
     */
    Map<Plugin, List<Class<? extends ButtonOption>>> getOptions();

    /**
     * Retrieves an Optional containing the class of the button option with the given name.
     *
     * @param name The name of the button option to retrieve.
     * @return An Optional containing the button option class if found, or an empty Optional if not found.
     */
    Optional<Class<? extends ButtonOption>> getOption(String name);

    /**
     * Sets the current page number for a given player in a paginated context. This method is useful for managing player-specific UI elements that require pagination, such as inventory screens or custom GUIs. It allows for the tracking and updating of the player's current page within a multi-page UI component.
     *
     * @param player  The {@link OfflinePlayer} instance representing the player whose page number is being set. This object allows for the operation to affect players who are not currently online as well.
     * @param page    The current page number to set for the player. This should be within the range of 1 to {@code maxPage}, inclusive.
     * @param maxPage The maximum number of pages available. This is used to validate the {@code page} parameter and ensure it does not exceed the available range of pages.
     */
    void setPlayerPage(OfflinePlayer player, int page, int maxPage);

    /**
     * Retrieves the current page number of a given player in a paginated context. This method is typically used in conjunction with {@link #setPlayerPage(OfflinePlayer, int, int)} to manage navigation through a multi-page UI component, allowing for a dynamic user experience based on the player's interaction.
     *
     * @param player The {@link OfflinePlayer} instance representing the player whose current page number is being queried. This enables the method to obtain information about players who are not currently online.
     * @return The current page number of the specified player. If the player's page has not been set previously, this method may return a default value or 0, depending on implementation.
     */
    int getPage(OfflinePlayer player);

    /**
     * Obtains the maximum page number set for a given player in a paginated context. This method allows for the retrieval of the upper limit of the pagination range for a player, facilitating UI boundary checks and preventing navigation beyond the available content.
     *
     * @param player The {@link OfflinePlayer} instance representing the player whose maximum page number is being retrieved. As with other methods, this supports offline players, allowing for a wide range of applications.
     * @return The maximum page number available for the specified player. Similar to {@link #getPage(OfflinePlayer)}, if the max page has not been set, this method might return a default value or 0, depending on how it's implemented.
     */
    int getMaxPage(OfflinePlayer player);

    /**
     * Retrieves a fake inventory for testing or simulation purposes.
     *
     * @return An instance of {@link InventoryEngine} representing the fake inventory.
     */
    InventoryEngine getFakeInventory();

    /**
     * Provides access to the enchantments management interface.
     *
     * @return An instance of {@link Enchantments} for managing custom enchantments.
     */
    Enchantments getEnchantments();

    /**
     * Retrieves the font image utility for managing custom fonts.
     *
     * @return An instance of {@link FontImage} for handling font images.
     */
    FontImage getFontImage();

    /**
     * Loads a YAML configuration file and applies global placeholders.
     *
     * @param file The YAML file to load.
     * @return The loaded YAML configuration.
     */
    YamlConfiguration loadYamlConfiguration(File file);

    /**
     * Loads an inventory element based on the specified requirement type and value.
     * This method interprets the given type and value to configure or adjust
     * inventory-related settings or behaviors.
     *
     * @param type  The type of inventory requirement to be loaded. This defines
     *              the specific aspect of the inventory being targeted.
     * @param value The value associated with the inventory requirement, used to
     *              configure or modify the inventory setting as per the type specified.
     */
    void loadElement(InventoryRequirementType type, String value);

    void registerInventoryOption(Plugin plugin, Class<? extends InventoryOption> inventoryOption);

    Map<Plugin, List<Class<? extends InventoryOption>>> getInventoryOptions();

    Optional<Class<? extends InventoryOption>> getInventoryOption(String name);

    void unregisterInventoryOptions(Plugin plugin);

    void registerInventoryListener(InventoryListener inventoryListener);

    void unregisterInventoryListener(InventoryListener inventoryListener);

    List<InventoryListener> getInventoryListeners();

    ItemStack postProcessSkullItemStack(ItemStack itemStack, Button button, Player player);

    void sendMessage(CommandSender sender, Message message, Object... args);

    void load();

    MenuPlugin getPlugin();

    /**
     * Loads a menu item stack from a YAML configuration and applies global placeholders.
     * This method loads the item stack using the given configuration and path, and
     * applies any global placeholders that have been registered.
     *
     * @param configuration the YAML configuration containing the item stack settings.
     * @param path          the path within the configuration to load from.
     * @param file          the file from which the configuration was loaded, used for logging errors.
     * @return the loaded menu item stack.
     */
    MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file);

    /**
     * Loads a menu item stack from a file, using the specified path and additional parameters.
     * <p>
     * This method reads the configuration data from the provided file and constructs
     * a MenuItemStack object based on the specified path within the file. The map
     * parameter allows for additional customization and overrides to be applied
     * during the loading process.
     *
     * @param file The file containing the item stack configuration data.
     * @param path The path within the file to locate the item stack data.
     * @param map  A map of additional parameters or overrides for customizing the item stack.
     * @return The loaded MenuItemStack object.
     * <p>
     */
    MenuItemStack loadItemStack(File file, String path, Map<String, Object> map);
}