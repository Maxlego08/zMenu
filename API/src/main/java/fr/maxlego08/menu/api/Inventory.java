package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The `Inventory` interface defines the structure and behavior of inventory-related operations.
 * <p>
 * Documentation: <a href="https://docs.zmenu.dev/api/create-inventory">Inventory documentation</a>
 * <p>This class encapsulates information about an inventory, including its size, buttons, patterns, and associated requirements.</p>
 * </p>
 */
public interface Inventory {

    /**
     * Returns the size of the inventory.
     *
     * @return The size of the inventory. This value is the number of slots in the inventory.
     */
    int size();

    /**
     * Returns the name of the inventory.
     *
     * @return The name of the inventory. This name is used internally by zMenu and can be used to identify the inventory.
     */
    String getName();

    /**
     * Returns the translated name of the inventory.
     *
     * @return The name of the inventory, translated to the player's language.
     */
    String getName(Player player, InventoryEngine InventoryEngine, Placeholders placeholders);

    /**
     * Returns the type of the inventory.
     *
     * @return The type of the inventory.
     */
    InventoryType getType();

    /**
     * Returns the name of the file associated with the inventory.
     *
     * @return The file name. This name is the name of the configuration file.
     */
    String getFileName();

    /**
     * Returns a collection of buttons present in the inventory.
     *
     * @return A collection of buttons. This collection contains all the buttons present in the inventory, including the ones in the patterns.
     */
    Collection<Button> getButtons();

    /**
     * Returns a collection of patterns associated with the inventory.
     *
     * @return A collection of patterns. This collection contains all the patterns associated with the inventory.
     */
    Collection<Pattern> getPatterns();

    /**
     * Returns a list of buttons of a specific type.
     *
     * @param type The class type of the button.
     * @param <T>  The button type.
     * @return A list of buttons with the specified type.
     */
    <T extends Button> List<T> getButtons(Class<T> type);

    /**
     * Returns the plugin from which the inventory originates.
     *
     * @return The plugin.
     */
    Plugin getPlugin();

    /**
     * Returns the maximum number of pages for the inventory.
     *
     * @param patterns Pattern list
     * @param player   The player for whom the page count is determined.
     * @param objects  Additional elements.
     * @return The maximum number of pages.
     */
    int getMaxPage(Collection<Pattern> patterns, Player player, Object... objects);

    /**
     * Sorts the buttons based on the current page and additional elements.
     *
     * @param page    The current page.
     * @param objects Additional elements.
     * @return A list of sorted buttons.
     */
    List<Button> sortButtons(int page, Object... objects);

    /**
     * Sorts the pattern buttons based on the current page and additional elements.
     *
     * @param pattern The pattern
     * @param page    The current page.
     * @param objects Additional elements.
     * @return A list of sorted buttons.
     */
    List<Button> sortPatterns(Pattern pattern, int page, Object... objects);

    /**
     * Opens the inventory for a player and returns the result of the operation.
     *
     * @param player          The player opening the inventory.
     * @param InventoryEngine The inventory to be opened.
     * @return The result of the inventory opening.
     */
    InventoryResult openInventory(Player player, InventoryEngine InventoryEngine);

    /**
     * Performs post-opening actions for the inventory.
     *
     * @param player          The player for whom post-opening actions are performed.
     * @param InventoryEngine The default inventory object.
     */
    void postOpenInventory(Player player, InventoryEngine InventoryEngine);

    /**
     * Closes the inventory for a player.
     *
     * @param player          The player closing the inventory.
     * @param InventoryEngine The inventory to be closed.
     */
    void closeInventory(Player player, InventoryEngine InventoryEngine);

    /**
     * Returns the item stack used to fill empty slots in the inventory.
     *
     * @return The fill item stack.
     */
    MenuItemStack getFillItemStack();

    /**
     * Returns the interval for updating buttons in the inventory.
     *
     * @return The update interval.
     */
    int getUpdateInterval();

    /**
     * Returns the configuration file associated with the inventory.
     *
     * @return The configuration file.
     */
    File getFile();

    /**
     * Determines whether the player's inventory should be cleared upon closing the inventory.
     *
     * @return True if the player's inventory should be cleaned, false otherwise.
     */
    boolean cleanInventory();

    /**
     * Returns the requirement for opening the inventory.
     *
     * @return The opening requirement.
     */
    Requirement getOpenRequirement();

    /**
     * Returns the item that can be used to open the inventory. (Left or rick click)
     *
     * @return The OpenWithItem
     */
    OpenWithItem getOpenWithItem();

    /**
     * Returns the translated name of the inventory.
     *
     * @return The translated name, a map of locale to translated name.
     */
    Map<String, String> getTranslatedNames();


    /**
     * Retrieves a list of all conditional names associated with this inventory.
     *
     * <p>These conditional names are used to determine whether a player can access the inventory, based on a
     * set of permissions.</p>
     *
     * @return A list of all conditional names associated with this inventory.
     */
    List<ConditionalName> getConditionalNames();
}