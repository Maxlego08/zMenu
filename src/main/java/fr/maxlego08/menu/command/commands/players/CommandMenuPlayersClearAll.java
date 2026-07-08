package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuPlayersClearAll extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersClearAll(ZMenuPlugin plugin) {
        super(plugin, "clearall", "ca");
        this.setPermission(Permission.ZMENU_PLAYERS_CLEAR_ALL.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        DataManager dataManager = commandDispatch.getPlugin().getDataManager();
        dataManager.clearAll();

        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_CLEAR_ALL);
        return CommandResultType.SUCCESS;
    }
}
