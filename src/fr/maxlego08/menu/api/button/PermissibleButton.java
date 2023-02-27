package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public interface PermissibleButton {

	/**
	 * Returns the button that will be used if the condition does not pass
	 * 
	 * @return else button
	 */
	public Button getElseButton();
	
	/**
	 * Returns the parent button
	 * 
	 * @return button
	 */
	public Button getParentButton();
	
	/**
	 * Return the real parent
	 * 
	 * @return button
	 */
	public Button getMasterParentButton();
	
	/**
	 * The permission that the player will have to have
	 * 
	 * @return permission
	 */
	public String getPermission();

	/**
	 * Allow to check if the permission is valid
	 * 
	 * @return true
	 */
	public boolean hasPermission();

	/**
	 * Allows to know if there is a button to display
	 * 
	 * @return boolean
	 */
	public boolean hasElseButton();
	
	/**
	 * Allows to check if the player has the permission
	 * 
	 * @param player
	 * @param inventory
	 * @return boolean
	 */
	public boolean checkPermission(Player player, InventoryDefault inventory);
	
	/**
	 * Allows to check if the player does not have the permissions
	 * 
	 * @return boolean
	 */
	public boolean isReverse();
	
}
