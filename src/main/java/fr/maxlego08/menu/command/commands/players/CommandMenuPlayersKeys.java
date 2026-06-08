package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.test.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CommandMenuPlayersKeys extends VCommand {

    public CommandMenuPlayersKeys(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_KEYS);
        this.addSubCommand("keys");
        this.addRequireArg("player");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        DataManager dataManager = plugin.getDataManager();

        OfflinePlayer player = this.argAsOfflinePlayer(0);

        Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
        if (optional.isEmpty()) {
            this.message(plugin, this.sender, Message.PLAYERS_DATA_KEYS_EMPTY, "%player%", player.getName());
            return CommandType.SUCCESS;
        }

        PlayerData playerData = optional.get();
        Collection<Data> collection = playerData.getDatas();

        if (collection.isEmpty()) {
            this.message(plugin, this.sender, Message.PLAYERS_DATA_KEYS_EMPTY);
            return CommandType.SUCCESS;
        }

        List<String> keyList = new ArrayList<>(collection.size());
        for (Data data : collection) {
            keyList.add(data.getKey());
        }
        String keys = this.toList(keyList, "§8", "§7");
        this.message(plugin, this.sender, Message.PLAYERS_DATA_KEYS_SUCCESS, "%keys%", keys, "%player%", player.getName());

        return CommandType.SUCCESS;
    }

}
