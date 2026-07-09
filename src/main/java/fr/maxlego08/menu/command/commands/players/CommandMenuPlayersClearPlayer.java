package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.argument.OfflinePlayerArgument;
import fr.robie.paperdispatch.cache.OfflinePlayerCache;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandMenuPlayersClearPlayer extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersClearPlayer(ZMenuPlugin plugin) {
        super(plugin, "clear");
        this.setPermission(Permission.ZMENU_PLAYERS_CLEAR_PLAYER.getPermission());
        this.addRequiredArgument("player", new OfflinePlayerArgument());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        DataManager dataManager = commandDispatch.getPlugin().getDataManager();

        UUID playerUUID = commandDispatch.getArgument("player", UUID.class);

        dataManager.clearPlayer(playerUUID);

        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_CLEAR_PLAYER, "%player%", OfflinePlayerCache.getGlobalInstance().getName(playerUUID));
        return CommandResultType.SUCCESS;
    }
}
