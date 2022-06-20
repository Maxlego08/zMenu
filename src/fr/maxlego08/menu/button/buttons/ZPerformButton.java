package fr.maxlego08.menu.button.buttons;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.button.buttons.PerformButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class ZPerformButton extends ZPlaceholderButton implements PerformButton {

	private final List<String> commands;

	private final List<String> consoleCommands;
	private final List<String> consoleRightCommands;
	private final List<String> consoleLeftCommands;

	private final List<String> consolePermissionCommands;
	private final String consolePermission;

	private final List<Action> actions;

	/**
	 * @param commands
	 * @param consoleCommands
	 * @param consoleRightCommands
	 * @param consoleLeftCommands
	 * @param consolePermissionCommands
	 * @param consolePermission
	 * @param actions
	 */
	public ZPerformButton(List<String> commands, List<String> consoleCommands, List<String> consoleRightCommands,
			List<String> consoleLeftCommands, List<String> consolePermissionCommands, String consolePermission,
			List<Action> actions) {
		super();
		this.commands = commands;
		this.consoleCommands = consoleCommands;
		this.consoleRightCommands = consoleRightCommands;
		this.consoleLeftCommands = consoleLeftCommands;
		this.consolePermissionCommands = consolePermissionCommands;
		this.consolePermission = consolePermission;
		this.actions = actions;
	}

	@Override
	public List<String> getCommands() {
		return this.commands;
	}

	@Override
	public List<String> getConsoleCommands() {
		return this.consoleCommands;
	}

	@Override
	public List<String> getConsolePermissionCommands() {
		return this.consolePermissionCommands;
	}

	@Override
	public String getConsolePermission() {
		return this.consolePermission;
	}

	@Override
	public List<String> getConsoleRightCommands() {
		return this.consoleRightCommands;
	}

	@Override
	public List<String> getConsoleLeftCommands() {
		return this.consoleLeftCommands;
	}

	@Override
	public void execute(Player player, ClickType type) {

		if (type.equals(ClickType.RIGHT)) {
			this.execute(player, player, this.consoleRightCommands);
		}

		if (type.equals(ClickType.LEFT)) {
			this.execute(player, player, this.consoleLeftCommands);
		}

		this.execute(player, player, this.commands);
		this.execute(Bukkit.getConsoleSender(), player, this.consoleCommands);

		if (this.consolePermission == null || player.hasPermission(this.consolePermission)) {
			this.execute(Bukkit.getConsoleSender(), player, this.consolePermissionCommands);
		}

		this.actions.stream().filter(action -> action.isClick(type)).forEach(action -> action.execute(player, type));
	}

	/**
	 * Allows you to execute a list of commands
	 * 
	 * @param executor
	 * @param player
	 * @param strings
	 */
	private void execute(CommandSender executor, Player player, List<String> strings) {
		strings.forEach(command -> {
			command = command.replace("%player%", player.getName());
			Bukkit.dispatchCommand(executor, papi(command, player));
		});
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		this.execute(player, event.getClick());
		super.onClick(player, event, inventory, slot);

	}

	@Override
	public List<Action> getActions() {
		return this.actions;
	}

}
