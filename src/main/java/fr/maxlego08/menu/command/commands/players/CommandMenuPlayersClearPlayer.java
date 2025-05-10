package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

public class CommandMenuPlayersClearPlayer extends VCommand {

    public CommandMenuPlayersClearPlayer(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_CLEAR_PLAYER);
        this.addSubCommand("clear");
        this.addRequireArg("player");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        DataManager dataManager = plugin.getDataManager();
        OfflinePlayer offlinePlayer = this.argAsOfflinePlayer(0);

        dataManager.clearPlayer(offlinePlayer.getUniqueId());

        message(plugin, this.sender, Message.PLAYERS_DATA_CLEAR_PLAYER, "%player%", offlinePlayer.getName());

        return CommandType.SUCCESS;
    }

}
