package fr.maxlego08.menu.zcore.utils.commands;

import java.util.function.BiConsumer;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;

public class ZCommand extends VCommand {

	public ZCommand(MenuPlugin plugin) {
		super(plugin);
	}

	private BiConsumer<VCommand, MenuPlugin> command;

	@Override
	public CommandType perform(MenuPlugin main) {
		
		if (command != null){
			command.accept(this, main);
		}

		return CommandType.SUCCESS;
	}

	public VCommand setCommand(BiConsumer<VCommand, MenuPlugin> command) {
		this.command = command;
		return this;
	}

	public VCommand sendHelp(String command) {
		this.command = (cmd, main) -> main.getCommandManager().sendHelp(command, cmd.getSender());
		return this;
	}

}
