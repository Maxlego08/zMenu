package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.command.OfflinePlayerArgument;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandMenuPlayersKeys extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayersKeys(ZMenuPlugin plugin) {
        super(plugin, "keys");
        this.setPermission(Permission.ZMENU_PLAYERS_KEYS.getPermission());
        this.addRequiredArgument("player", new OfflinePlayerArgument());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        DataManager dataManager = commandDispatch.getPlugin().getDataManager();

        UUID targetId = commandDispatch.getArgument("player", UUID.class);
        Optional<PlayerData> optional = dataManager.getPlayer(targetId);
        if (optional.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_KEYS_EMPTY, "%player%", OfflinePlayerCache.getName(targetId));
            return CommandResultType.SUCCESS;
        }

        PlayerData playerData = optional.get();
        Collection<Data> collection = playerData.getDatas();

        if (collection.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_KEYS_EMPTY, "%player%", OfflinePlayerCache.getName(targetId));
            return CommandResultType.SUCCESS;
        }

        List<String> keyList = new ArrayList<>(collection.size());
        for (Data data : collection) {
            keyList.add(data.getKey());
        }
        String keys = ZUtils.toList(keyList, "§8", "§7");
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.PLAYERS_DATA_KEYS_SUCCESS, "%keys%", keys, "%player%", OfflinePlayerCache.getName(targetId));
        return CommandResultType.SUCCESS;
    }
}
