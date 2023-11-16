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
 * Documentation: <a href="https://docs.zmenu.dev/api/create-inventory">https://docs.zmenu.dev/api/create-inventory</a>
 * <p>This class will contain all the inventory information.</p>
 */
public interface Inventory {

    /**
     * Returns the size of the inventory
     *
     * @return size
     */
    int size();

    /**
     * Returns the name of the inventory
     *
     * @return name
     */
    String getName();

    /**
     * Returns the name of the file
     *
     * @return fileName
     */
    String getFileName();

    /**
     * Return the list of buttons
     *
     * @return buttons
     */
    Collection<Button> getButtons();

    /**
     * Returns inventory patterns
     *
     * @return patterns
     */
    Collection<Pattern> getPatterns();

    /**
     * Returns the list of buttons according to a type
     *
     * @param type Class type
     * @param <T> Button type
     * @return list of button with this type
     */
    <T extends Button> List<T> getButtons(Class<T> type);

    /**
     * Returns the plugin where the inventory comes from
     *
     * @return plugin
     */
    Plugin getPlugin();

    /**
     * Returns the maximum number of pages
     *
     * @param objects elements
     * @return page
     */
    int getMaxPage(Player player, Object... objects);

    /**
     * Allows you to sort the buttons according to a page
     *
     * @param page    current page
     * @param objects elements
     * @return buttons
     */
    List<Button> sortButtons(int page, Object... objects);

    /**
     * When an inventory is open
     *
     * @param player           The player who will open the inventory
     * @param inventoryDefault The inventory that will be opened
     * @return the result of the opening of the inventory
     */
    InventoryResult openInventory(Player player, VInventory inventoryDefault);

    /**
     * @param player           Plugin
     * @param inventoryDefault default inventory object
     */
    void postOpenInventory(Player player, VInventory inventoryDefault);

    /**
     * When an inventory is close
     *
     * @param player           The player who will close the inventory
     * @param inventoryDefault The inventory that will be closed
     */
    void closeInventory(Player player, VInventory inventoryDefault);

    /**
     * Returns the itemstack that will be used to fill the inventory
     *
     * @return itemstack
     */
    MenuItemStack getFillItemStack();

    /**
     * Returns the interval for updating the buttons
     *
     * @return interval
     */
    int getUpdateInterval();

    /**
     * Returns the configuration file.
     *
     * @return file
     */
    File getFile();

    /**
     * Delete the player's inventory and give it back to him when he closes the inventory
     *
     * @return boolean
     */
    boolean cleanInventory();

    /**
     * Element carried out when opening the inventory
     *
     * @return Requirement
     */
    Requirement getOpenRequirement();

}
