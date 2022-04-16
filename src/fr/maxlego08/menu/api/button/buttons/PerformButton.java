package fr.maxlego08.menu.api.button.buttons;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.maxlego08.menu.api.button.PlaceholderButton;

public interface PerformButton extends PlaceholderButton {

	/**
	 * 
	 * @return commands list
	 */
	public List<String> getCommands();

	/**
	 * 
	 * @return console commands
	 */
	public List<String> getConsoleCommands();

	/**
	 * 
	 * @return console commands
	 */
	public List<String> getConsoleRightCommands();

	/**
	 * 
	 * @return console commands
	 */
	public List<String> getConsoleLeftCommands();

	/**
	 * 
	 * @return
	 */
	public List<String> getConsolePermissionCommands();

	/**
	 * 
	 * @return
	 */
	public String getConsolePermission();

	/**
	 * 
	 * @param player
	 */
	void execute(Player player, ClickType type);

}
