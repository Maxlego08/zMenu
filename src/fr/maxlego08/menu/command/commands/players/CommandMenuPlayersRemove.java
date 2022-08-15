package fr.maxlego08.menu.command.commands.players;

import java.util.Optional;

import org.bukkit.OfflinePlayer;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuPlayersRemove extends VCommand {

	public CommandMenuPlayersRemove(MenuPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZMENU_PLAYERS);
		this.setDescription(Message.DESCRIPTION_PLAYERS_REMOVE);
		this.addSubCommand("remove");
		this.addRequireArg("player");
		this.addRequireArg("key");
	}

	@Override
	protected CommandType perform(MenuPlugin plugin) {

		DataManager dataManager = plugin.getDataManager();

		OfflinePlayer player = this.argAsOfflinePlayer(0);
		String key = this.argAsString(1);

		Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
		if (!optional.isPresent()) {
			message(this.sender, Message.PLAYERS_DATA_REMOVE_ERROR);
			return CommandType.SUCCESS;
		}

		PlayerData playerData = optional.get();
		if (!playerData.containsKey(key)) {
			message(this.sender, Message.PLAYERS_DATA_REMOVE_ERROR);
			return CommandType.SUCCESS;
		}

		playerData.removeData(key);
		message(this.sender, Message.PLAYERS_DATA_REMOVE_SUCCESS, "%key%", key, "%player%", player.getName());

		return CommandType.SUCCESS;
	}

}
