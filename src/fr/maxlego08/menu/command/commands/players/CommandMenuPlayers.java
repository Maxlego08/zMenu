package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuPlayers extends VCommand {

	public CommandMenuPlayers(MenuPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZMENU_PLAYERS);
		this.setDescription(Message.DESCRIPTION_PLAYERS);
		this.addSubCommand("players");
		this.addSubCommand(new CommandMenuPlayersSet(plugin));
		this.addSubCommand(new CommandMenuPlayersGet(plugin));
		this.addSubCommand(new CommandMenuPlayersRemove(plugin));
		this.addSubCommand(new CommandMenuPlayersKeys(plugin));
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		message(this.sender, Message.DOCUMENTATION_INFORMATION);
		this.subVCommands.forEach(command -> {
			if (command.getPermission() == null || this.sender.hasPermission(command.getPermission())) {
				message(this.sender, Message.COMMAND_SYNTAXE_HELP, "%syntax%", command.getSyntax(), "%description%",
						command.getDescription());
			}
		});

		return CommandType.SUCCESS;
	}

}
