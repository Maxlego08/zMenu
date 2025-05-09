package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class CommandMenuPlayersSubtract extends VCommand {

    public CommandMenuPlayersSubtract(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_SUBTRACT);
        this.addSubCommand("subtract");
        this.addRequireArg("player");
        this.addRequireArg("key", (sender, args) -> {
            ZDataManager dataManager = (ZDataManager) plugin.getDataManager();
            return dataManager.getKeys(args);
        });
        this.addRequireArg("number");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        OfflinePlayer player = this.argAsOfflinePlayer(0);
        String key = this.argAsString(1);
        int value = this.argAsInteger(2);

        DataManager dataManager = plugin.getDataManager();
        Optional<Data> optional = dataManager.getData(player.getUniqueId(), key);
        if (!optional.isPresent()) {
            Data data = new ZData(key, -value, 0);
            dataManager.addData(player.getUniqueId(), data);
        } else {
            Data data = optional.get();
            data.remove(value);
        }
        dataManager.autoSave();

        message(this.sender, Message.PLAYERS_DATA_SUBTRACT, "%player%", player.getName(), "%key%", key);

        return CommandType.SUCCESS;
    }

}
