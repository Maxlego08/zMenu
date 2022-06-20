package fr.maxlego08.menu.api.action;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.maxlego08.menu.api.action.permissible.Permissible;

public interface Action {

	/**
	 * Return the list of clicktype 
	 * 
	 * @return types
	 */
	List<ClickType> getClickType();
	
	/**
	 * Returns the action if the player has permission
	 * 
	 * @return {@link ActiondClick}
	 */
	ActiondClick getAllowAction();
	
	/**
	 * Returns the action if the player does not have permission
	 * 
	 * @return {@link ActiondClick}
	 */
	ActiondClick getDenyAction();
	
	/**
	 * Returns the list of permissions that the player must have
	 * 
	 * @return permissibles
	 */
	List<Permissible> getPermissibles();

	/**
	 * Allows to execute the action
	 * 
	 * @param player
	 * @param type
	 */
	public void execute(Player player, ClickType type);
	
	/**
	 * Allows to check if a click is possible
	 * 
	 * @param type
	 * @return boolean
	 */
	public boolean isClick(ClickType type);
}
