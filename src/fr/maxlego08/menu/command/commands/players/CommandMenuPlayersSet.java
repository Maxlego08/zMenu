package fr.maxlego08.menu.command.commands.players;

import org.bukkit.OfflinePlayer;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuPlayersSet extends VCommand {

	public CommandMenuPlayersSet(MenuPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZMENU_PLAYERS);
		this.setDescription(Message.DESCRIPTION_PLAYERS_SET);
		this.addSubCommand("set");
		this.addRequireArg("player");
		this.addRequireArg("key");
		this.addRequireArg("expire after");
		this.addRequireArg("value");
		this.setExtendedArgs(true);
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		OfflinePlayer player = this.argAsOfflinePlayer(0);
		String key = this.argAsString(1);
		long seconds = this.argAsLong(2);

		System.out.print(args.length);

		if (this.args.length < 6) {
			return CommandType.SYNTAX_ERROR;
		}

		StringBuilder builder = new StringBuilder();
		for (int index = 5; index < this.args.length; index++) {
			builder.append(this.args[index]).append(" ");
		}

		if (builder.toString().isEmpty()){
			return CommandType.SYNTAX_ERROR;
		}
		
		String value = builder.substring(0, builder.length() - 1);
		System.out.print(value);

		long expiredAt = System.currentTimeMillis() + (1000 * seconds);
		Data data = new ZData(key, value, expiredAt);
		
		DataManager dataManager = plugin.getDataManager();
		dataManager.addData(player.getUniqueId(), data);
		
		message(this.sender, Message.DESCRIPTION_PLAYERS_SET, "%player%", player.getName(), "%key%", key);
		
		return CommandType.SUCCESS;
	}

}
