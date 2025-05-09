package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandMenuPlayersKeys extends VCommand {

    public CommandMenuPlayersKeys(MenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_KEYS);
        this.addSubCommand("keys");
        this.addRequireArg("player");
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        DataManager dataManager = plugin.getDataManager();

        OfflinePlayer player = this.argAsOfflinePlayer(0);

        Optional<PlayerData> optional = dataManager.getPlayer(player.getUniqueId());
        if (!optional.isPresent()) {
            message(this.sender, Message.PLAYERS_DATA_KEYS_EMPTY, "%player%", player.getName());
            return CommandType.SUCCESS;
        }

        PlayerData playerData = optional.get();
        Collection<Data> collection = playerData.getDatas();

        if (collection.isEmpty()) {
            message(this.sender, Message.PLAYERS_DATA_KEYS_EMPTY);
            return CommandType.SUCCESS;
        }

        String keys = toList(collection.stream().map(Data::getKey).collect(Collectors.toList()), "§8", "§7");
        message(this.sender, Message.PLAYERS_DATA_KEYS_SUCCESS, "%keys%", keys, "%player%", player.getName());

        return CommandType.SUCCESS;
    }

}
