package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuPlayersClearAll extends VCommand {

	public CommandMenuPlayersClearAll(MenuPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZMENU_PLAYERS);
		this.setDescription(Message.DESCRIPTION_PLAYERS_CLEARALL);
		this.addSubCommand("clearall");
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		DataManager dataManager = plugin.getDataManager();
		dataManager.clearAll();
		
		message(this.sender, Message.PLAYERS_DATA_CLEARALL);
		
		return CommandType.SUCCESS;
	}

}
