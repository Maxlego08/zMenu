package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenu extends VCommand {

	public CommandMenu(MenuPlugin plugin) {
		super(plugin);
		this.addSubCommand(new CommandMenuOpen(plugin));
		this.addSubCommand(new CommandMenuReload(plugin));
		this.addSubCommand(new CommandMenuVersion(plugin));
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		this.subVCommands.forEach(command -> {
			if (command.getPermission() == null || this.sender.hasPermission(command.getPermission())) {
				message(this.sender, Message.COMMAND_SYNTAXE_HELP, "%syntax%", command.getSyntax(), "%description%",
						command.getDescription());
			}
		});

		return CommandType.SUCCESS;
	}

}
