package fr.maxlego08.menu.api.button;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public interface Button {

	/**
	 * Returns the name of the button
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns the itemstack that will be displayed
	 * 
	 * @return {@link ItemStack}
	 */
	public ItemStack getItemStack();

	/**
	 * This method will return the itemstack that will be used in the inventory
	 * with a player in parameter
	 * 
	 * @param player
	 *            Player who opens the inventory
	 * @return {@link ItemStack}
	 */
	public ItemStack getCustomItemStack(Player player);

	/**
	 * Returns the slot used by the button
	 * 
	 * @return slot
	 */
	public int getSlot();

	/**
	 * Allows to know if the button can be clicked
	 * 
	 * @return boolean
	 */
	public boolean isClickable();

	/**
	 * Allows to know if the button is permanent A permanent button will always
	 * be present in the inventory no matter the page
	 * 
	 * @return boolean
	 */
	public boolean isPermament();

	/**
	 * Allows buttons that are going to be rendered in multiple slots to be
	 * rendered correctly
	 * 
	 * @return boolean
	 */
	public boolean hasSpecialRender();

	/**
	 * Allows you to make the buttons special
	 * 
	 * @param player
	 * @param inventory
	 */
	public void onRender(Player player, InventoryDefault inventory);

	/**
	 * Allows to change the type of a button
	 * 
	 * @param classz
	 * @return T
	 */
	public <T extends Button> T toButton(Class<T> classz);

	/**
	 * This method is called when the player makes a click
	 * 
	 * @param player
	 *            Player who will perform the click
	 * @param event
	 *            Event that will be called
	 * @param inventoryDefault
	 *            Inventory where the button comes from
	 */
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

	/**
	 * This method is called when the player makes a right click
	 * 
	 * @param player
	 *            Player who will perform the click
	 * @param event
	 *            Event that will be called
	 * @param inventory
	 *            Inventory where the button comes from
	 */
	public void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

	/**
	 * This method is called when the player makes a left click
	 * 
	 * @param player
	 *            Player who will perform the click
	 * @param event
	 *            Event that will be called
	 * @param inventory
	 *            Inventory where the button comes from
	 */
	public void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

	/**
	 * This method is called when the player makes a middle click or a drop key
	 * click
	 * 
	 * @param player
	 *            Player who will perform the click
	 * @param event
	 *            Event that will be called
	 * @param inventory
	 *            Inventory where the button comes from
	 */
	public void onMiddleClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot);

	/**
	 * This method is called when the player open the inventory
	 * 
	 * @param player
	 * @param inventory
	 */
	public void onInventoryOpen(Player player, InventoryDefault inventory);

	/**
	 * This method is called when the player close the inventory
	 * 
	 * @param player
	 * @param inventory
	 */
	public void onInventoryClose(Player player, InventoryDefault inventory);

	/**
	 * Allows to get the real slot of the button
	 * 
	 * @param inventorySize
	 * @param page
	 * @return slot
	 */
	public int getRealSlot(int inventorySize, int page);

	/**
	 * Allows you to know if you have to close the inventory when clicking
	 * 
	 * @return boolean
	 */
	public boolean closeInventory();

	/**
	 * Messages that the player will receive by clicking
	 * 
	 * @return messages
	 */
	public List<String> getMessages();

	/**
	 * Sound that will be played when the player clicks
	 * 
	 * @return sound
	 */
	public SoundOption getSound();
	
	/**
	 * Return the player name
	 * 
	 * @return name
	 */
	public String getPlayerHead();
	
	/**
	 * Allows you to open a link in a message
	 * 
	 * @return openLink
	 */ 
	public OpenLink getOpenLink();

}
