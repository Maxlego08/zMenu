package fr.maxlego08.menu.command.commands;

import java.util.Optional;
import java.util.stream.Collectors;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuReload extends VCommand {

	public CommandMenuReload(MenuPlugin plugin) {
		super(plugin);
		this.addSubCommand("reload", "rl");
		this.setDescription(Message.DESCRIPTION_RELOAD);
		this.setPermission(Permission.ZMENU_RELOAD);
		this.addOptionalArg("menu", (a, b) -> plugin.getInventoryManager().getInventories().stream().map(e -> {
			return (e.getPlugin().getName() + ":" + e.getFileName()).toLowerCase();
		}).collect(Collectors.toList()));
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		String inventoryName = this.argAsString(0, null);
		InventoryManager inventoryManager = plugin.getInventoryManager();

		if (inventoryName != null) {
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

			Inventory inventory = optional.get();
			plugin.getVInventoryManager().close(v -> {
				InventoryDefault inventoryDefault = (InventoryDefault)v;
				return !inventoryDefault.isClose() && inventoryDefault.getInventory().equals(inventory);
			});
			inventoryManager.reloadInventory(inventory);
			message(this.sender, Message.RELOAD_FILE, "%name%", inventoryName);

			return CommandType.SUCCESS;
		}

		plugin.getMessageLoader().load(plugin.getPersist());
		Config.getInstance().load(plugin.getPersist());

		plugin.getVInventoryManager().close();
		
		inventoryManager.deleteInventories(plugin);
		inventoryManager.loadInventories();

		CommandManager commandManager = plugin.getCommandManager();
		commandManager.loadCommands();

		message(this.sender, Message.RELOAD);

		return CommandType.SUCCESS;
	}

}
