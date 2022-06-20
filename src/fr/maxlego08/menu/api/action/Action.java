package fr.maxlego08.menu.api.action;

import java.util.List;

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
	 * @return {@link Action}
	 */
	Action getAllowAction();
	
	/**
	 * Returns the action if the player does not have permission
	 * 
	 * @return {@link Action}
	 */
	Action getDenyAction();
	
	/**
	 * Returns the list of permissions that the player must have
	 * 
	 * @return permissibles
	 */
	List<Permissible> getPermissibles();
}
