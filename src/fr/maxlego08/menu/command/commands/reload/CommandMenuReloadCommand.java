package fr.maxlego08.menu.command.commands.reload;

import java.util.Optional;
import java.util.stream.Collectors;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuReloadCommand extends VCommand {

	public CommandMenuReloadCommand(MenuPlugin plugin) {
		super(plugin);
		this.addSubCommand("command", "cmd");
		this.setPermission(Permission.ZMENU_RELOAD);
		this.addOptionalArg("command", (a, b) -> plugin.getCommandManager().getCommands().stream().map(e -> e.getCommand().toLowerCase()).collect(Collectors.toList()));
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		String commandName = this.argAsString(0, null);
		CommandManager commandManager = plugin.getCommandManager();

		if (commandName != null) {
			Optional<Command> optional = commandManager.getCommand(commandName);

			if (!optional.isPresent()) {
				message(this.sender, Message.INVENTORY_OPEN_ERROR_COMMAND, "%name%", commandName);
				return CommandType.DEFAULT;
			}

			Command command = optional.get();
			plugin.getVInventoryManager().close(v -> {
				InventoryDefault inventoryDefault = (InventoryDefault) v;
				return !inventoryDefault.isClose() && inventoryDefault.getInventory().equals(command.getInventory());
			});
			
			Message message = commandManager.reload(command) ? Message.RELOAD_COMMAND_FILE
					: Message.RELOAD_COMMAND_ERROR;
			message(this.sender, message, "%name%", commandName);

			return CommandType.SUCCESS;
		}

		plugin.getMessageLoader().load(plugin.getPersist());
		Config.getInstance().load(plugin.getPersist());

		plugin.getVInventoryManager().close();

		commandManager.loadCommands();

		message(this.sender, Message.RELOAD_COMMAND);

		return CommandType.SUCCESS;
	}

}
