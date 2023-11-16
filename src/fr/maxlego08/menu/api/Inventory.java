package fr.maxlego08.menu.api;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.List;

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
     * @return The size of the inventory.
     */
    int size();

    /**
     * Returns the name of the inventory.
     *
     * @return The name of the inventory.
     */
    String getName();

    /**
     * Returns the name of the file associated with the inventory.
     *
     * @return The file name.
     */
    String getFileName();

    /**
     * Returns a collection of buttons present in the inventory.
     *
     * @return A collection of buttons.
     */
    Collection<Button> getButtons();

    /**
     * Returns a collection of patterns associated with the inventory.
     *
     * @return A collection of patterns.
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
     * @param player  The player for whom the page count is determined.
     * @param objects Additional elements.
     * @return The maximum number of pages.
     */
    int getMaxPage(Player player, Object... objects);

    /**
     * Sorts the buttons based on the current page and additional elements.
     *
     * @param page    The current page.
     * @param objects Additional elements.
     * @return A list of sorted buttons.
     */
    List<Button> sortButtons(int page, Object... objects);

    /**
     * Opens the inventory for a player and returns the result of the operation.
     *
     * @param player           The player opening the inventory.
     * @param inventoryDefault The inventory to be opened.
     * @return The result of the inventory opening.
     */
    InventoryResult openInventory(Player player, VInventory inventoryDefault);

    /**
     * Performs post-opening actions for the inventory.
     *
     * @param player           The player for whom post-opening actions are performed.
     * @param inventoryDefault The default inventory object.
     */
    void postOpenInventory(Player player, VInventory inventoryDefault);

    /**
     * Closes the inventory for a player.
     *
     * @param player           The player closing the inventory.
     * @param inventoryDefault The inventory to be closed.
     */
    void closeInventory(Player player, VInventory inventoryDefault);

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
}
