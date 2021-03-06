package fr.maxlego08.menu.command.commands;

import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuOpen extends VCommand {

	public CommandMenuOpen(MenuPlugin plugin) {
		super(plugin);
		this.addSubCommand("open", "o");

		InventoryManager inventoryManager = plugin.getInventoryManager();
		this.addRequireArg("inventory name", (a, b) -> inventoryManager.getInventories().stream().map(e -> {
			return (e.getPlugin().getName() + ":" + e.getFileName()).toLowerCase();
		}).collect(Collectors.toList()));

		this.addOptionalArg("player");
		this.setDescription(Message.DESCRIPTION_OPEN);
		this.setPermission(Permission.ZMENU_OPEN);
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		InventoryManager inventoryManager = plugin.getInventoryManager();

		String inventoryName = this.argAsString(0);
		Player player = this.argAsPlayer(1, this.player);
		if (player == null) {
			message(this.sender, sender instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE
					: Message.INVENTORY_OPEN_ERROR_PLAYER);
			return CommandType.DEFAULT;
		}

		Optional<Inventory> optional;
		if (inventoryName.contains(":")) {
			String[] values = inventoryName.split(":");
			if (values.length == 2) {
				optional = inventoryManager.getInventory(values[0], values[1]);
			} else {
				optional = inventoryManager.getInventory(inventoryName);
			}
		} else {
			optional = inventoryManager.getInventory(inventoryName);
		}

		if (!optional.isPresent()) {
			message(this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
			return CommandType.DEFAULT;
		}

		if (this.sender == player) {
			message(this.sender, Message.INVENTORY_OPEN_SUCCESS, "%name%", inventoryName);
		} else {
			message(this.sender, Message.INVENTORY_OPEN_OTHER, "%name%", inventoryName, "%player%", player.getName());
		}

		Inventory inventory = optional.get();
		inventoryManager.openInventory(player, inventory);

		return CommandType.SUCCESS;
	}

}
