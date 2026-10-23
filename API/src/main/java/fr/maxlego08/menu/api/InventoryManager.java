package fr.maxlego08.menu.api;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.animation.slot.AnimationManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.button.GenericPaginationButton;
import fr.maxlego08.menu.api.checker.InventoryRequirementType;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.ButtonLoaderRegisterEvent;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.itemstack.ItemStackService;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.itemstack.MenuItemStackConversion;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import fr.maxlego08.menu.api.pagination.PaginationManager;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.RegistryManager;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

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
     * Returns an inventory by its key, {@code plugin:file}.
     *
     * <p>Build the key with {@link ZMenuKeys#fileKey(String, String)}, or read a reference written
     * by a user with {@link #findInventory(String)}.</p>
     *
     * @param key the key of the inventory
     * @return Optional of {@link Inventory}.
     */
    Optional<Inventory> getInventory(@NotNull Key key);

    /**
     * Returns the keys of every loaded inventory.
     *
     * @return the keys, {@code plugin:file}
     */
    Set<Key> getInventoryKeys();

    /**
     * Returns an optional of {@link Inventory} based on its name.
     *
     * @param name Inventory name.
     * @return Optional of {@link Inventory}.
     * @deprecated a file name and the key of its inventory can differ (case, spaces). Use
     * {@link #getInventory(Key)}, or {@link #findInventory(String)} for a name written by a user.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    Optional<Inventory> getInventory(String name);

    /**
     * Returns an optional of {@link Inventory} based on its name and the plugin.
     *
     * @param plugin The plugin where the inventory comes from.
     * @param name   Inventory name.
     * @return Optional of {@link Inventory}.
     * @deprecated use {@link #getInventory(Key)} with {@link ZMenuKeys#fileKey(String, String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    Optional<Inventory> getInventory(Plugin plugin, String name);

    /**
     * Returns an optional of {@link Inventory} based on its name and the plugin name.
     *
     * @param pluginName The plugin name where the inventory comes from.
     * @param name       Inventory name.
     * @return Optional of {@link Inventory}.
     * @deprecated use {@link #getInventory(Key)} with {@link ZMenuKeys#fileKey(String, String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * Finds an inventory from a reference written by a user, in a file or a command:
     * {@code plugin:file} is looked up exactly, while a plain {@code file} is looked up in zMenu
     * first, then in the other plugins.
     *
     * @param inventoryName the reference
     * @return Optional of {@link Inventory}.
     * @see ZMenuKeys#inventoryKey(String)
     */
    Optional<Inventory> findInventory(@NotNull String inventoryName);

    /**
     * Deletes an inventory.
     *
     * @param inventory {@link Inventory} to be deleted.
     */
    void deleteInventory(Inventory inventory);

    /**
     * Deletes an inventory by its key.
     *
     * @param key the key of the inventory
     * @return True if the inventory has been deleted.
     */
    boolean deleteInventory(@NotNull Key key);

    /**
     * Deletes an inventory based on its name.
     *
     * @param name Inventory name.
     * @return True if the inventory has been deleted.
     * @deprecated use {@link #deleteInventory(Key)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    boolean deleteInventory(String name);

    /**
     * Deletes all inventories loaded from a plugin.
     *
     * @param plugin The plugin.
     */
    void deleteInventories(Plugin plugin);

    /**
     * @return the inventories as {@code plugin:file}, lowercased
     * @deprecated use {@link #getInventoryKeys()}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    Set<String> getInventoryNames();

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
     * @deprecated use {@link #openInventory(Player, Inventory, int, List)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default void openInventory(Player player, Inventory inventory, int page, Inventory... inventories) {
        this.openInventory(player, inventory, page, new ArrayList<>(Arrays.asList(inventories)));
    }

    /**
     * Loads buttons. The {@link ButtonLoaderRegisterEvent} event will be
     * called, allowing you to add your own buttons using this event.
     *
     * @deprecated part of zMenu's startup, not meant to be called by other plugins. Register your
     * buttons with {@link ButtonManager#register(fr.maxlego08.menu.api.loader.ButtonLoader)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * @deprecated register it in {@link Registries#MATERIAL_LOADER}:
     * {@code registries.writable(Registries.MATERIAL_LOADER).register(plugin, loader)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    boolean registerMaterialLoader(MaterialLoader materialLoader);

    /**
     * Returns a material loader based on a key.
     *
     * @param key The key to identify the material loader.
     * @return Optional of {@link MaterialLoader}.
     * @deprecated read {@link Registries#MATERIAL_LOADER} with {@link ZMenuKeys#parse(String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    Optional<MaterialLoader> getMaterialLoader(String key);

    /**
     * Returns a list of material loaders.
     *
     * @return List of {@link MaterialLoader}.
     * @deprecated read {@link Registries#MATERIAL_LOADER}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    Collection<MaterialLoader> getMaterialLoader();

    /**
     * Opens an inventory by its key. When it does not exist, the player's inventory is closed and
     * they are told so.
     *
     * @param player Player who will open the inventory.
     * @param key    the key of the inventory
     */
    void openInventory(Player player, @NotNull Key key);

    /**
     * Opens an inventory by its key, on a page.
     *
     * @param player Player who will open the inventory.
     * @param key    the key of the inventory
     * @param page   The page number of the inventory to open.
     */
    void openInventory(Player player, @NotNull Key key, int page);

    /**
     * Opens an inventory based on its name and the plugin.
     *
     * @param player        Player who will open the inventory.
     * @param plugin        The plugin where the inventory comes from.
     * @param inventoryName Name of the inventory to be opened.
     * @deprecated use {@link #openInventory(Player, Key)} with {@link ZMenuKeys#fileKey(String, String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void openInventory(Player player, Plugin plugin, String inventoryName);

    /**
     * Opens an inventory based on its name and the plugin name.
     *
     * @param player        Player who will open the inventory.
     * @param pluginName    The plugin where the inventory comes from.
     * @param inventoryName Name of the inventory to be opened.
     * @deprecated use {@link #openInventory(Player, Key)} with {@link ZMenuKeys#fileKey(String, String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void openInventory(Player player, String pluginName, String inventoryName);

    /**
     * Opens an inventory based on its name and the plugin name.
     *
     * @param player        Player who will open the inventory.
     * @param pluginName    The plugin where the inventory comes from.
     * @param inventoryName Name of the inventory to be opened.
     * @param page          The page number of the inventory to open.
     * @deprecated use {@link #openInventory(Player, Key, int)} with {@link ZMenuKeys#fileKey(String, String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void openInventory(Player player, String pluginName, String inventoryName, int page);

    /**
     * Opens an inventory based on its name. Note that
     * this method searches in all inventories.
     *
     * @param player        Player who will open the inventory.
     * @param inventoryName Name of the inventory to be opened.
     * @deprecated use {@link #openInventory(Player, Key)}, or {@link #findInventory(String)} for a
     * name written by a user.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void openInventory(Player player, String inventoryName);

    /**
     * Opens an inventory based on its name and a specific page number.
     *
     * @param player        Player who will open the inventory.
     * @param inventoryName Name of the inventory to be opened.
     * @param page          The page number of the inventory to open.
     * @deprecated use {@link #openInventory(Player, Key, int)}, or {@link #findInventory(String)}
     * for a name written by a user.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void openInventory(Player player, String inventoryName, int page);

    /**
     * Returns an optional of a plugin based on its name.
     *
     * @param pluginName The name of the plugin to be found.
     * @return Optional of a plugin that can contain the plugin if it exists.
     * @deprecated not related to inventories; use {@link org.bukkit.plugin.PluginManager#getPlugin(String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * @deprecated use {@link MenuPlugin#getMetaUpdater()}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * @deprecated the {@code /zm list} command, not meant to be called by other plugins; read the
     * inventories with {@link #getInventories()}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void sendInventories(CommandSender sender);

    /**
     * Creates a new inventory file.
     *
     * @param sender        Command sender.
     * @param fileName      Inventory file name.
     * @param inventorySize Inventory size.
     * @param inventoryName Inventory name.
     * @deprecated the {@code /zm create} command, not meant to be called by other plugins.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * @deprecated the {@code /zm save} command, not meant to be called by other plugins; use
     * {@link ItemStackService#fromItemStack(ItemStack)} to turn an item into a menu item.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void saveItem(CommandSender sender, ItemStack itemStack, String name, String type);

    /**
     * Turns an item into a menu item, with item components for every setting the item changes.
     * When a setting cannot be expressed, the whole item is kept as {@code base64:} so that nothing is lost.
     *
     * @param itemStack The item to convert.
     * @return The menu item, and the components that forced the fallback.
     * @deprecated use {@link ItemStackService#fromItemStack(ItemStack)}.
     */
    @NotNull
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default MenuItemStackConversion toMenuItemStack(@NotNull ItemStack itemStack) {
        return this.getPlugin().getItemStackService().fromItemStack(itemStack);
    }

    /**
     * Transforms a string list into a click list type.
     * If the list contains ANY or ALL then the ClickType present in the plugin configuration will be added to the list.
     *
     * @param loadClicks - Click type as string
     * @return Loaded click type
     * @deprecated an internal helper of the menu loaders, not related to inventories.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    List<ClickType> loadClicks(List<String> loadClicks);

    /**
     * Registers an ItemStackSimilar instance for verifying ItemStacks.
     * This method allows for adding a new ItemStack verification strategy to the system.
     *
     * @param itemStackSimilar The ItemStackSimilar instance to be registered.
     * @deprecated register it in {@link Registries#ITEM_VERIFICATION}:
     * {@code registries.writable(Registries.ITEM_VERIFICATION).register(plugin, comparison)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default void registerItemStackVerification(ItemStackSimilar itemStackSimilar) {
        this.getRegistries().writable(Registries.ITEM_VERIFICATION).register(JavaPlugin.getProvidingPlugin(itemStackSimilar.getClass()), itemStackSimilar);
    }

    /**
     * Retrieves an Optional ItemStackSimilar instance based on its name.
     * This method is used to get a specific ItemStack verification strategy by its unique name.
     * If no verification strategy is found with the given name, an empty Optional is returned.
     *
     * @param name The name of the ItemStack verification strategy to retrieve.
     * @return An Optional containing the ItemStackSimilar instance if found, or an empty Optional otherwise.
     * @deprecated use {@link ItemStackService#getVerification(String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default Optional<ItemStackSimilar> getItemStackVerification(String name) {
        return this.getPlugin().getItemStackService().getVerification(name);
    }

    /**
     * Returns a collection of all registered ItemStackSimilar instances.
     * This method is used to get all the available ItemStack verification strategies currently registered.
     * The collection contains instances of ItemStackSimilar, each representing a different verification strategy.
     *
     * @return A Collection of ItemStackSimilar instances representing all registered verification strategies.
     * @deprecated read {@link Registries#ITEM_VERIFICATION}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default Collection<ItemStackSimilar> getItemStackVerifications() {
        return this.getRegistries().registry(Registries.ITEM_VERIFICATION).values();
    }

    /**
     * Gets the scheduler associated with this button option.
     *
     * @return The ZScheduler associated with this button option.
     * @deprecated use {@link MenuPlugin#getScheduler()}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * @deprecated use {@link PaginationManager#setMenuPage(UUID, int, int)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default void setPlayerPage(OfflinePlayer player, int page, int maxPage) {
        this.getPaginationManager().setMenuPage(player.getUniqueId(), page, maxPage);
    }

    /**
     * Retrieves the current page number of a given player in a paginated context. This method is typically used in conjunction with {@link #setPlayerPage(OfflinePlayer, int, int)} to manage navigation through a multi-page UI component, allowing for a dynamic user experience based on the player's interaction.
     *
     * @param player The {@link OfflinePlayer} instance representing the player whose current page number is being queried. This enables the method to obtain information about players who are not currently online.
     * @return The current page number of the specified player. If the player's page has not been set previously, this method may return a default value or 0, depending on implementation.
     * @deprecated use {@link PaginationManager#getMenuPage(UUID)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default int getPage(OfflinePlayer player) {
        return this.getPaginationManager().getMenuPage(player.getUniqueId());
    }

    /**
     * Obtains the maximum page number set for a given player in a paginated context. This method allows for the retrieval of the upper limit of the pagination range for a player, facilitating UI boundary checks and preventing navigation beyond the available content.
     *
     * @param player The {@link OfflinePlayer} instance representing the player whose maximum page number is being retrieved. As with other methods, this supports offline players, allowing for a wide range of applications.
     * @return The maximum page number available for the specified player. Similar to {@link #getPage(OfflinePlayer)}, if the max page has not been set, this method might return a default value or 0, depending on how it's implemented.
     * @deprecated use {@link PaginationManager#getMenuMaxPage(UUID)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default int getMaxPage(OfflinePlayer player) {
        return this.getPaginationManager().getMenuMaxPage(player.getUniqueId());
    }

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
     * @deprecated use {@link MenuPlugin#getEnchantments()}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    Enchantments getEnchantments();

    /**
     * Retrieves the font image utility for managing custom fonts.
     *
     * @return An instance of {@link FontImage} for handling font images.
     * @deprecated use {@link MenuPlugin#getFontImage()}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    FontImage getFontImage();

    /**
     * Loads a YAML configuration file and applies global placeholders.
     *
     * @param file The YAML file to load.
     * @return The loaded YAML configuration.
     * @deprecated an internal helper of the menu loaders, not related to inventories.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
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
     * @deprecated part of zMenu's loading of menus that wait for a button, an action or a
     * pattern, not meant to be called by other plugins.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void loadElement(InventoryRequirementType type, String value);

    void registerInventoryOption(Plugin plugin, Class<? extends InventoryOption> inventoryOption);

    Map<Plugin, List<Class<? extends InventoryOption>>> getInventoryOptions();

    Optional<Class<? extends InventoryOption>> getInventoryOption(String name);

    void unregisterInventoryOptions(Plugin plugin);

    void registerInventoryListener(InventoryListener inventoryListener);

    void unregisterInventoryListener(InventoryListener inventoryListener);

    List<InventoryListener> getInventoryListeners();

    /**
     * Gives a player head the skin of the player named by the {@code player-head} of a button.
     *
     * @param itemStack the head
     * @param button    the button
     * @param player    the player
     * @return the head
     * @deprecated use {@link ItemStackService#applyPlayerHead(ItemStack, Button, Player, Placeholders)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default ItemStack postProcessSkullItemStack(ItemStack itemStack, Button button, Player player) {
        return this.getPlugin().getItemStackService().applyPlayerHead(itemStack, button, player, new Placeholders());
    }

    /**
     * Gives a player head the skin of the player named by the {@code player-head} of a button.
     *
     * @param itemStack    the head
     * @param button       the button
     * @param player       the player
     * @param placeholders the placeholders of the inventory
     * @return the head
     * @deprecated use {@link ItemStackService#applyPlayerHead(ItemStack, Button, Player, Placeholders)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default ItemStack postProcessSkullItemStack(ItemStack itemStack, Button button, Player player, Placeholders placeholders) {
        return this.getPlugin().getItemStackService().applyPlayerHead(itemStack, button, player, placeholders);
    }

    /**
     * Sends one of zMenu's messages.
     *
     * @param sender  the receiver
     * @param message the message
     * @param args    the placeholders, as pairs of key and value
     * @deprecated zMenu's messages are not meant to be sent by other plugins.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void sendMessage(CommandSender sender, Message message, Object... args);

    /**
     * Loads the buttons, patterns, animations and inventories.
     *
     * @deprecated part of zMenu's startup, not meant to be called by other plugins; use
     * {@link #loadInventories()} to read the menus again.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void load();

    /**
     * Gets the plugin this manager belongs to.
     *
     * @return the plugin
     */
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
     * @deprecated use {@link ItemStackService#load(YamlConfiguration, String, File)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default MenuItemStack loadItemStack(YamlConfiguration configuration, String path, File file) {
        return this.getPlugin().getItemStackService().load(configuration, path, file);
    }

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
     * @deprecated the path is not used; use {@link ItemStackService#load(File, Map)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default MenuItemStack loadItemStack(File file, String path, Map<String, Object> map) {
        return this.getPlugin().getItemStackService().load(file, map);
    }

    /**
     * Loads a menu item stack from a map of its settings.
     *
     * @param file the file the map was read from
     * @param map  the settings of the item
     * @return the menu item stack
     * @deprecated use {@link ItemStackService#load(File, Map)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default MenuItemStack loadItemStack(File file, Map<String, Object> map) {
        return this.getPlugin().getItemStackService().load(file, map);
    }

    /**
     * Provides access to the pagination manager for handling paginated content in inventories.
     *
     * <p>The PaginationManager is responsible for managing multi-page inventory displays,
     * allowing buttons to paginate through large collections of items or data. It tracks
     * the current page for each player and manages navigation between pages.</p>
     *
     * <p>This is typically used in conjunction with {@link GenericPaginationButton} or
     * other paginated button implementations to display collections that exceed a single
     * inventory page's capacity.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * PaginationManager manager = inventoryManager.getPaginationManager();
     * // Use manager to control pagination state
     * }</pre>
     *
     * @return An instance of {@link PaginationManager} for managing pagination state in inventories.
     * @see GenericPaginationButton
     * @see PaginationManager
     */
    PaginationManager getPaginationManager();

    /**
     * Gets the manager holding the named animations: the files of the {@code animations} folder and
     * the ones registered by plugins.
     *
     * <p>Use it to fetch an animation, or to register one of your own:</p>
     *
     * <pre>
     * inventoryManager.getAnimationManager().registerAnimation(plugin, new Animation(Key.key("myplugin", "my_animation"), settings));
     * </pre>
     *
     * @return The animation manager.
     * @see AnimationManager
     */
    AnimationManager getAnimationManager();

    /**
     * Gets every registry of zMenu, such as the animations an inventory may use as its {@code type}.
     *
     * <p>Read an entry, and register your own from your {@code onEnable}:</p>
     *
     * <pre>
     * inventoryManager.getRegistries().registry(Registries.SLOT_ANIMATION).get(ZMenuKeys.key("spiral"));
     * inventoryManager.getRegistries().writable(Registries.MY_REGISTRY).register(plugin, myEntry);
     * </pre>
     *
     * <p>Static registries stop accepting entries once the server has finished loading.</p>
     *
     * @return The registries.
     * @see fr.maxlego08.menu.api.registry.Registries
     */
    RegistryManager getRegistries();

    /**
     * Registers an animation menus may use as their {@code type}, under its key and its aliases.
     * Call it from your {@code onEnable}:
     *
     * <pre>
     * inventoryManager.registerSlotAnimation(plugin, new MyAnimationLoader());
     * </pre>
     *
     * @param plugin the plugin registering it, shown when two animations share a name
     * @param loader the loader
     * @throws fr.maxlego08.menu.api.registry.RegistrationException if a name is taken, or the server has finished loading
     */
    default void registerSlotAnimation(@NotNull Plugin plugin, @NotNull SlotAnimationLoader loader) {
        WritableRegistry<SlotAnimationLoader> registry = this.getRegistries().writable(Registries.SLOT_ANIMATION);
        registry.register(plugin, loader);
        for (Key alias : loader.aliases()) {
            registry.alias(plugin.getName(), alias, loader.key());
        }
    }

    /**
     * Writes an item as a base64 string.
     *
     * @param itemStack the item
     * @return the item as base64
     * @deprecated use {@link ItemStackService#serialize(ItemStack)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default String serializeItemStack(ItemStack itemStack) {
        return this.getPlugin().getItemStackService().serialize(itemStack);
    }

    /**
     * Reads an item written as a base64 string.
     *
     * @param serializedItemStack the item as base64
     * @return the item
     * @deprecated use {@link ItemStackService#deserialize(String)}.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default ItemStack deserializeItemStack(String serializedItemStack) {
        return this.getPlugin().getItemStackService().deserialize(serializedItemStack);
    }

    default SoundOption createSoundOption(String soundName, String categoryName, float pitch, float volume) {
        return this.createSoundOption(soundName, categoryName, pitch, volume, false);
    }

    SoundOption createSoundOption(String soundName, String categoryName, float pitch, float volume, boolean isCustom);

}