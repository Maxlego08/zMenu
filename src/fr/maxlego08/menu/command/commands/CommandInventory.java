package fr.maxlego08.menu.command.commands;

import java.util.Optional;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandInventory extends VCommand {

	private final Command command;

	/**
	 * @param plugin
	 * @param command
	 */
	public CommandInventory(MenuPlugin plugin, Command command) {
		super(plugin);
		this.command = command;
		this.setPermission(command.getPermission());
		this.setConsoleCanUse(false);
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		String inventoryName = this.command.getInventory();
		InventoryManager manager = plugin.getInventoryManager();
		Optional<Inventory> optional = manager.getInventory(inventoryName);
		if (optional.isPresent()) {
			manager.openInventory(this.player, optional.get());
		} else {
			message(player, Message.INVENTORY_ERROR, "%name%", inventoryName);
		}

		return CommandType.SUCCESS;
	}

}
