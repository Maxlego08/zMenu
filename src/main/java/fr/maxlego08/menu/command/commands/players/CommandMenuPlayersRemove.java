package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class CommandMenuPlayersRemove extends VCommand {

    public CommandMenuPlayersRemove(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_REMOVE);
        this.addSubCommand("remove");
        this.addRequireArg("player");
        this.addRequireArg("key", (sender, args) -> {
            ZDataManager dataManager = (ZDataManager) plugin.getDataManager();
            return dataManager.getKeys(args);
        });
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        DataManager dataManager = plugin.getDataManager();

        OfflinePlayer player = this.argAsOfflinePlayer(0);
        String key = this.argAsString(1);

        Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
        if (optional.isEmpty()) {
            message(plugin, this.sender, Message.PLAYERS_DATA_REMOVE_ERROR, "%key%", key);
            return CommandType.SUCCESS;
        }

        PlayerData playerData = optional.get();
        if (!playerData.containsKey(key)) {
            message(plugin, this.sender, Message.PLAYERS_DATA_REMOVE_ERROR, "%key%", key);
            return CommandType.SUCCESS;
        }

        playerData.removeData(key);
        message(plugin, this.sender, Message.PLAYERS_DATA_REMOVE_SUCCESS, "%key%", key, "%player%", player.getName());

        return CommandType.SUCCESS;
    }

}
