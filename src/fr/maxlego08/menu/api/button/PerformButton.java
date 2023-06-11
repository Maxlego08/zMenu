package fr.maxlego08.menu.api.button;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.maxlego08.menu.api.action.Action;

public interface PerformButton {

	/**
	 * Returns the list of commands that the player will execute
	 * 
	 * @return commands list
	 */
	public List<String> getCommands();

	/**
	 * Returns the list of console that the player will execute
	 * 
	 * @return console commands
	 */
	public List<String> getConsoleCommands();

	/**
	 * Returns the list of commands that the console will execute if click is
	 * right
	 * 
	 * @return console commands
	 */
	public List<String> getConsoleRightCommands();

	/**
	 * Returns the list of commands that the console will execute if click is
	 * left
	 * 
	 * @return console commands
	 */
	public List<String> getConsoleLeftCommands();

	/**
	 * Returns the list of commands that the console will execute if player has
	 * permission
	 * 
	 * @return console commands
	 */
	public List<String> getConsolePermissionCommands();

	/**
	 * Returns the permission the player must have to use the console permission
	 * 
	 * @return permissions
	 */
	public String getConsolePermission();

	/**
	 * Permet d'exécuter les commandes
	 * 
	 * @param player
	 * @param type
	 */
	public void execute(Player player, ClickType type);
	
	/**
	 * Returns the list of actions that will be executed
	 * It is recommended to use actions. Actions will offer more possibilities when clicking.
	 * 
	 * @return actions
	 */
	public List<Action> getActions();

}
