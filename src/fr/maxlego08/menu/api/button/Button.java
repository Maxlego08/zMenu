package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.requirement.Action;
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
 * <p>The Button interface defines methods for creating customizable buttons in zMenu. Buttons are a key feature of zMenu, providing a unified way for plugins to create inventory buttons with shared configurations across the server.</p>
 * <p>For detailed information on creating a button, refer to the <a href="https://docs.zmenu.dev/api/create-button">zMenu documentation</a>.</p>
 */
public interface Button extends PermissibleButton, PlaceholderButton, SlotButton, PerformButton {

    /**
     * Returns the name of the button.
     *
     * @return The name of the button.
     */
    String getName();

    /**
     * Returns the ItemStack that will be displayed.
     *
     * @return The {@link MenuItemStack}.
     */
    MenuItemStack getItemStack();

    /**
     * Returns the ItemStack that will be used in the inventory with a player in parameter.
     *
     * @param player The player who opens the inventory.
     * @return The {@link ItemStack}.
     */
    ItemStack getCustomItemStack(Player player);

    /**
     * Returns the slot used by the button.
     *
     * @return The slot.
     */
    int getSlot();

    /**
     * Checks if the button can be clicked.
     *
     * @return True if the button is clickable, false otherwise.
     */
    boolean isClickable();

    /**
     * Checks if the button is permanent. A permanent button will always be present in the inventory, regardless of the page.
     *
     * @return True if the button is permanent, false otherwise.
     */
    boolean isPermanent();

    /**
     * Checks if the button has special rendering requirements.
     *
     * @return True if the button has special rendering, false otherwise.
     */
    boolean hasSpecialRender();

    /**
     * Allows buttons that are going to be rendered in multiple slots to be rendered correctly.
     *
     * @param player    The player.
     * @param inventory The inventory object containing all the information of the current inventory.
     */
    void onRender(Player player, InventoryDefault inventory);

    /**
     * Called when the player makes a click.
     *
     * @param player    The player who performs the click.
     * @param event     The event that will be called.
     * @param inventory The inventory where the button comes from.
     * @param slot      The current slot.
     */
    void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * Called when the player makes a right click.
     *
     * @param player    The player who performs the click.
     * @param event     The event that will be called.
     * @param inventory The inventory where the button comes from.
     */
    void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * Called when the player makes a left click.
     *
     * @param player    The player who performs the click.
     * @param event     The event that will be called.
     * @param inventory The inventory where the button comes from.
     */
    void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * Called when the player makes a middle click or a drop key click.
     *
     * @param player    The player who performs the click.
     * @param event     The event that will be called.
     * @param inventory The inventory where the button comes from.
     */
    void onMiddleClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

    /**
     * Called when the player opens the inventory.
     *
     * @param player    The player.
     * @param inventory The inventory.
     */
    void onInventoryOpen(Player player, InventoryDefault inventory);

    /**
     * Called when the player closes the inventory.
     *
     * @param player    The player.
     * @param inventory The inventory.
     */
    void onInventoryClose(Player player, InventoryDefault inventory);

    /**
     * Allows to get the real slot of the button.
     *
     * @param inventorySize The size of the inventory.
     * @param page          The current page.
     * @return The real slot.
     */
    int getRealSlot(int inventorySize, int page);

    /**
     * Checks if the inventory should be closed when clicking.
     *
     * @return True if the inventory should be closed, false otherwise.
     */
    boolean closeInventory();

    /**
     * Gets the messages that the player will receive by clicking.
     *
     * @return The list of messages.
     */
    List<String> getMessages();

    /**
     * Gets the sound that will be played when the player clicks.
     *
     * @return The {@link SoundOption}.
     */
    SoundOption getSound();

    /**
     * Gets the player name.
     *
     * @return The player name.
     */
    String getPlayerHead();

    /**
     * Allows to open a link in a message.
     *
     * @return The {@link OpenLink}.
     */
    OpenLink getOpenLink();

    /**
     * Checks if the button needs to be updated.
     *
     * @return True if the button needs to be updated, false otherwise.
     */
    boolean isUpdated();

    /**
     * Checks if the button should be refreshed on click.
     *
     * @return True if the button should be refreshed on click, false otherwise.
     */
    boolean isRefreshOnClick();

    /**
     * Gets the action player data.
     *
     * @return The list of {@link ActionPlayerData}.
     */
    List<ActionPlayerData> getData();

    /**
     * Checks if the button should be updated when someone clicks on the inventory.
     *
     * @return True if the button should be updated, false otherwise.
     */
    boolean updateOnClick();

    /**
     * Builds the display name for auto-update.
     *
     * @param player The player.
     * @return The display name.
     */
    String buildDisplayName(Player player);

    /**
     * Builds the lore for auto-update.
     *
     * @param player The player.
     * @return The lore.
     */
    List<String> buildLore(Player player);

    /**
     * Performs an action when a back button is clicked.
     *
     * @param player          The player.
     * @param event           The inventory click event.
     * @param inventory       The current inventory.
     * @param oldInventories  The list of old inventories.
     * @param toInventory     The inventory to open.
     * @param slot            The current slot.
     */
    void onBackClick(Player player, InventoryClickEvent event, InventoryDefault inventory, List<Inventory> oldInventories, Inventory toInventory, int slot);

    /**
     * Gets the list of requirements made when clicking.
     *
     * @return The list of {@link Requirement}.
     */
    List<Requirement> getClickRequirements();

    /**
     * Gets the requirement that will be used to display the button.
     *
     * @return The {@link Requirement}.
     */
    Requirement getViewRequirement();

    /**
     * Returns the list of actions performed on click
     *
     * @return actions
     */
    List<Action> getActions();
}
