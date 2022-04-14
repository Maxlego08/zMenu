package fr.maxlego08.menu.api.buttons;

import org.bukkit.entity.Player;

public interface PermissibleButton extends Button {

	/**
	 * Returns the button that will be used if the condition does not pass
	 * 
	 * @return else button
	 */
	public Button getElseButton();
	
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
	 * @return boolean
	 */
	public boolean checkPermission(Player player);
	
}
