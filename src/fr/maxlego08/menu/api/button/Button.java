package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * <p>One of the great features of zMenu is its {@link Button} customization. You will be able to create buttons for your plugins, which will allow users to have only one inventory plugin for the whole server. The goal of zMenu is to make as many plugins as possible use its API, so that users have only one type of inventory configuration for their entire server.</p>
 * <p>To create a {@link Button} you will need at least two classes. A first one for the button and a second one to change the button. You can also create an interface to implement your button, but this is not mandatory.</p>
 * <p>More information <a href="https://docs.zmenu.dev/api/create-button">here</a></p>
 */
public interface Button extends PermissibleButton, PlaceholderButton, SlotButton, PerformButton {

    /**
     * Returns the name of the button
     *
     * @return name
     */
    String getName();

    /**
     * Returns the itemstack that will be displayed
     *
     * @return {@link MenuItemStack}
     */
    MenuItemStack getItemStack();

    /**
     * This method will return the itemstack that will be used in the inventory
     * with a player in parameter
     *
     * @param player Player who opens the inventory
     * @return {@link ItemStack}
     */
    ItemStack getCustomItemStack(Player player);

    /**
     * Returns the slot used by the button
     *
     * @return slot
     */
    int getSlot();

    /**
     * Allows to know if the button can be clicked
     *
     * @return boolean
     */
    boolean isClickable();

    /**
     * Allows to know if the button is permanent A permanent button will always
     * be present in the inventory no matter the page
     *
     * @return boolean
     */
    boolean isPermament();

    /**
     * Allows buttons that are going to be rendered in multiple slots to be
     * rendered correctly
     *
     * @return boolean
     */
    boolean hasSpecialRender();

    /**
     * Allows you to make the buttons special
     *
     * @param player    The player
     * @param inventory The inventory, object that will contain all the information of the current inventory
     */
    void onRender(Player player, InventoryDefault inventory);

    /**
     * This method is called when the player makes a click
     *
     * @param player    Player who will perform the click
     * @param event     Event that will be called
     * @param inventory Inventory where the button comes from
     * @param slot      current slot
     */
    void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * This method is called when the player makes a right click
     *
     * @param player    Player who will perform the click
     * @param event     Event that will be called
     * @param inventory Inventory where the button comes from
     */
    void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * This method is called when the player makes a left click
     *
     * @param player    Player who will perform the click
     * @param event     Event that will be called
     * @param inventory Inventory where the button comes from
     */
    void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * This method is called when the player makes a middle click or a drop key
     * click
     *
     * @param player    Player who will perform the click
     * @param event     Event that will be called
     * @param inventory Inventory where the button comes from
     */
    void onMiddleClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * This method is called when the player open the inventory
     *
     * @param player
     * @param inventory
     */
    void onInventoryOpen(Player player, InventoryDefault inventory);

    /**
     * This method is called when the player close the inventory
     *
     * @param player
     * @param inventory
     */
    void onInventoryClose(Player player, InventoryDefault inventory);

    /**
     * Allows to get the real slot of the button
     *
     * @param inventorySize
     * @param page
     * @return slot
     */
    int getRealSlot(int inventorySize, int page);

    /**
     * Allows you to know if you have to close the inventory when clicking
     *
     * @return boolean
     */
    boolean closeInventory();

    /**
     * Messages that the player will receive by clicking
     *
     * @return messages
     */
    List<String> getMessages();

    /**
     * Sound that will be played when the player clicks
     *
     * @return sound
     */
    SoundOption getSound();

    /**
     * Return the player name
     *
     * @return name
     */
    String getPlayerHead();

    /**
     * Allows you to open a link in a message
     *
     * @return openLink
     */
    OpenLink getOpenLink();

    /**
     * Lets you know if the button needs to be updated
     *
     * @return boolean
     */
    boolean isUpdated();

    /**
     * Update
     *
     * @return boolean
     */
    boolean isRefreshOnClick();

    /**
     * Return action player data
     *
     * @return datas
     */
    List<ActionPlayerData> getData();

    /**
     * Updates the button when someone clicks on the inventory
     *
     * @return boolean
     */
    boolean updateOnClick();

    /**
     * Display name use with auto update
     *
     * @param player
     * @return display name
     */
    String buildDisplayName(Player player);

    /**
     * Lore use with auto update
     *
     * @param player
     * @return lore
     */
    List<String> buildLore(Player player);

    /**
     * Perform an action when a back button is clicked
     *
     * @param player         The player
     * @param event          The inventory click event
     * @param inventory      The current inventory
     * @param oldInventories Old Inventory list
     * @param toInventory    Inventory to open
     * @param slot           Current slot
     */
    void onBackClick(Player player, InventoryClickEvent event, InventoryDefault inventory, List<Inventory> oldInventories, Inventory toInventory, int slot);

    /**
     * List of requirements made when clicking
     *
     * @return list of {@link Requirement}
     */
    List<Requirement> getClickRequirements();

    /**
     * Returns the requirement that will be used to display the button
     *
     * @return Requirement
     */
    Requirement getViewRequirement();
}
