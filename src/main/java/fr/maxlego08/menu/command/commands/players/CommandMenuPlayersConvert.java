package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandMenuPlayersConvert extends SubCommand<ZMenuPlugin> {

    private final Map<UUID, Long> confirmationMap = new HashMap<>();

    public CommandMenuPlayersConvert(ZMenuPlugin plugin) {
        super(plugin, "convert", "c");
        this.setPermission(Permission.ZMENU_PLAYERS_CONVERT.getPermission());
        this.setPlayerOnly();
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        UUID playerId = commandDispatch.getPlayer().getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (this.confirmationMap.containsKey(playerId) && (currentTime - this.confirmationMap.get(playerId)) <= 30000) {
            this.confirmationMap.remove(playerId);
            commandDispatch.getPlugin().getDataManager().convertOldDatas(commandDispatch.getSender());
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_CONVERT_SUCCESS);
        } else {
            this.confirmationMap.put(playerId, currentTime);
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_CONVERT_CONFIRM);
            commandDispatch.getPlugin().getScheduler().runLater(() -> this.confirmationMap.remove(playerId), 30, TimeUnit.SECONDS);
        }
        return CommandResultType.SUCCESS;
    }
}