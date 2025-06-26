package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuPlayersRemoveAll extends VCommand {

    public CommandMenuPlayersRemoveAll(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_REMOVE_ALL);
        this.addSubCommand("removeall");
        this.addRequireArg("key", (a, b) -> plugin.getDataManager().getKeys());
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        DataManager dataManager = plugin.getDataManager();
        String key = this.argAsString(0);
        dataManager.clearKey(key);
        message(plugin, this.sender, Message.PLAYERS_DATA_REMOVE_ALL_SUCCESS, "%key%", key);

        return CommandType.SUCCESS;
    }

}
