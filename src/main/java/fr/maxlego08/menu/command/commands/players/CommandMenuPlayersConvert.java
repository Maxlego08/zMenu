package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CommandMenuPlayersConvert extends VCommand {

    private final Map<UUID, Long> confirmationMap = new HashMap<>();

    public CommandMenuPlayersConvert(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_CONVERT);
        this.addSubCommand("convert");
        this.setConsoleCanUse(false);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        UUID playerId = this.player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (this.confirmationMap.containsKey(playerId) && (currentTime - confirmationMap.get(playerId)) <= 30000) {

            this.confirmationMap.remove(playerId);
            plugin.getDataManager().convertOldDatas(sender);
            message(plugin, this.sender, Message.PLAYERS_DATA_CONVERT_SUCCESS);

        } else {

            this.confirmationMap.put(playerId, currentTime);
            message(plugin, this.sender, Message.PLAYERS_DATA_CONVERT_CONFIRM);

            plugin.getScheduler().runLater(() -> this.confirmationMap.remove(playerId), 30, TimeUnit.SECONDS);
        }

        return CommandType.SUCCESS;
    }
}