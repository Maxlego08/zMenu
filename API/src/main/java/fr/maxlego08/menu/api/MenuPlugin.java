package fr.maxlego08.menu.api;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.attribute.AttributApplier;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.interfaces.ReturnBiConsumer;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.toast.ToastHelper;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface MenuPlugin extends Plugin {

    /**
     * Retrieves the scheduler associated with this plugin.
     * This scheduler is responsible for running tasks asynchronously.
     *
     * @return the scheduler.
     */
    PlatformScheduler getScheduler();

    /**
     * Retrieves the manager responsible for handling inventory-related operations.
     * This manager provides methods for creating, managing and retrieving inventories.
     *
     * @return the inventory manager.
     */
    InventoryManager getInventoryManager();

    /**
     * Retrieves the manager responsible for handling button-related operations.
     * This manager provides methods for managing button states and interactions.
     *
     * @return the button manager.
     */
    ButtonManager getButtonManager();

    /**
     * Returns the instance of the pattern manager.
     *
     * @return the pattern manager.
     */
    PatternManager getPatternManager();

    /**
     * Retrieves a service provider of the specified class type.
     *
     * @param classPath the class type of the provider to retrieve.
     * @param <T>       the type of the service provider.
     */
    <T> T getProvider(Class<T> classPath);

    /**
     * Replaces placeholders in the given string with actual values for the given player.
     * Placeholders are specified in the format %placeholder% and are replaced with actual values.
     * If the placeholder does not exist, the placeholder is kept as is.
     *
     * @param player the player to substitute placeholders for.
     * @param string the string to replace placeholders in.
     * @return a string with placeholders replaced.
     */
    String parse(Player player, String string);

    /**
     * Replaces placeholders in the given string with actual values for the given offline player.
     * Placeholders are specified in the format %placeholder% and are replaced with actual values.
     * If the placeholder does not exist, the placeholder is kept as is.
     *
     * @param offlinePlayer the offline player to substitute placeholders for.
     * @param string        the string to replace placeholders in.
     * @return a string with placeholders replaced.
     */
    String parse(OfflinePlayer offlinePlayer, String string);

    /**
     * Replaces placeholders in a list of strings with actual values for the given player.
     * Placeholders are specified in the format %placeholder% and are replaced with actual values.
     * If the placeholder does not exist, the placeholder is kept as is.
     *
     * @param player  the player to substitute placeholders for.
     * @param strings the list of strings to replace placeholders in.
     * @return a list of strings with placeholders replaced.
     */
    List<String> parse(Player player, List<String> strings);

    /**
     * Replaces placeholders in a list of strings with actual values for the given offline player.
     * Placeholders are specified in the format %placeholder% and are replaced with actual values.
     * If the placeholder does not exist, the placeholder is kept as is.
     *
     * @param offlinePlayer the player to substitute placeholders for.
     * @param strings       the list of strings to replace placeholders in.
     * @return a list of strings with placeholders replaced.
     */
    List<String> parse(OfflinePlayer offlinePlayer, List<String> strings);

    /**
     * Retrieves the manager responsible for player inventory related operations.
     * This manager provides methods for managing player inventory state,
     * such as saving, loading, and clearing inventories.
     *
     * @return The manager responsible for player inventory related operations.
     */
    InventoriesPlayer getInventoriesPlayer();

    /**
     * Retrieves a map of global placeholders.
     * This method returns a map where the keys are placeholder strings
     * and the values are the corresponding objects.
     * These placeholders are used for dynamic content replacement
     * throughout the plugin.
     *
     * @return A map containing global placeholders and their values.
     */
    Map<String, Object> getGlobalPlaceholders();

    /**
     * Retrieves the font image utility for managing custom fonts.
     *
     * @return An instance of {@link FontImage} for handling font images.
     */
    FontImage getFontImage();

    /**
     * Returns the data manager.
     * This method returns the data manager, which is used for managing data related to players.
     * The data manager is used for managing data related to players.
     *
     * @return the data manager
     */
    DataManager getDataManager();

    /**
     * Returns the dupe manager.
     * This method returns the dupe manager, which is responsible for managing
     * the duplication detection and protection logic within the plugin.
     * The dupe manager can be used to check if items are duplicated and to
     * apply protection against duplication exploits.
     *
     * @return the dupe manager
     */
    DupeManager getDupeManager();

    /**
     * Returns the enchantments manager.
     * This method returns the enchantments manager, which is used for managing custom enchantments.
     * The enchantments manager is used for managing custom enchantments.
     *
     * @return the enchantments manager
     */
    Enchantments getEnchantments();

    /**
     * Returns the meta updater.
     * This method returns the meta updater, which is used for updating the item meta.
     * The meta updater is used for updating the item meta.
     *
     * @return the meta updater
     */
    MetaUpdater getMetaUpdater();

    /**
     * Returns the command manager.
     * This method returns the command manager, which is used for registering and
     * executing commands.
     * The command manager is used for registering and executing commands.
     *
     * @return the command manager
     */
    CommandManager getCommandManager();

    /**
     * Returns the storage manager.
     * This method returns the storage manager, which is used for storing and retrieving data.
     * The storage manager is used for storing and retrieving data from the database or other
     * storage systems.
     *
     * @return the storage manager
     */
    StorageManager getStorageManager();

    /**
     * Checks if the plugin is a Folia plugin.
     * This method returns a boolean indicating whether the plugin is a Folia plugin.
     * This method is useful for checking if the plugin is a Folia plugin before using any
     * methods that require a Folia plugin.
     *
     * @return true if the plugin is a Folia plugin, false otherwise
     */
    boolean isFolia();

    /**
     * Checks if the plugin is a Paper plugin.
     * @return true if the plugin is a Paper plugin, false otherwise
     */
    boolean isPaper();

    /**
     * Checks if the plugin is a Paper or Folia plugin.
     * @return true if the plugin is a Paper or Folia plugin, false otherwise
     */
    boolean isPaperOrFolia();

    /**
     * Checks if the plugin is a Spigot plugin.
     * @return true if the plugin is a Spigot plugin, false otherwise
     */
    boolean isSpigot();

    /**
     * Registers a placeholder that can be used globally in the plugin.
     * This method allows you to register a placeholder that can be used in any menu item stack.
     * The placeholder is specified by the given startWith string, and the biConsumer parameter
     * is a function that takes an OfflinePlayer and returns a string. The return value of this
     * function is the replacement string for the given placeholder.
     *
     * @param startWith  the startWith string for the placeholder.
     * @param biConsumer the function that provides the replacement string for the placeholder.
     */
    void registerPlaceholder(String startWith, ReturnBiConsumer<OfflinePlayer, String, String> biConsumer);

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
     * Returns the ToastHelper object for this plugin.
     * The ToastHelper allows you to create and send toasts to players.
     *
     * @return the ToastHelper object.
     */
    ToastHelper getToastHelper();

    DialogManager getDialogManager();

    ItemManager getItemManager();

    AttributApplier getAttributApplier();

    TitleAnimationManager getTitleAnimationManager();

    ComponentsManager getComponentsManager();

    VInvManager getVInventoryManager();
}
