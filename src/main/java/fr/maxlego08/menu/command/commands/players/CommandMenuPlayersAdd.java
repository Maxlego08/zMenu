package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.players.ZDataManager;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Optional;

public class CommandMenuPlayersAdd extends VCommand {

    public CommandMenuPlayersAdd(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_ADD);
        this.addSubCommand("add");
        this.addRequireArg("player");
        this.addRequireArg("key", (sender, args) -> {
            ZDataManager dataManager = (ZDataManager) plugin.getDataManager();
            return dataManager.getKeys(args);
        });
        this.addRequireArg("number", (a, b) -> Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        OfflinePlayer offlinePlayer = this.argAsOfflinePlayer(0);
        String key = this.argAsString(1);
        int value = this.argAsInteger(2);

        DataManager dataManager = plugin.getDataManager();
        Optional<Data> optional = dataManager.getData(offlinePlayer.getUniqueId(), key);
        if (optional.isEmpty()) {
            Data data = new ZData(key, value, 0);
            dataManager.addData(offlinePlayer.getUniqueId(), data);
        } else {
            Data data = optional.get();
            data.add(value);
            plugin.getStorageManager().upsertData(offlinePlayer.getUniqueId(), data);
        }

        message(plugin, this.sender, Message.PLAYERS_DATA_ADD, "%player%", offlinePlayer.getName(), "%key%", key);

        return CommandType.SUCCESS;
    }

}
