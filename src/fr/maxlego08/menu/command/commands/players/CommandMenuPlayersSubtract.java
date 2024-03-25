package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.OfflinePlayer;

import java.util.Optional;

public class CommandMenuPlayersSubtract extends VCommand {

    public CommandMenuPlayersSubtract(MenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS_ADD);
        this.addSubCommand("CommandMenuPlayersAdd");
        this.addRequireArg("player");
        this.addRequireArg("key");
        this.addRequireArg("number");
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        OfflinePlayer player = this.argAsOfflinePlayer(0);
        String key = this.argAsString(1);
        int value = this.argAsInteger(2);

        Optional<Data> optional = plugin.getDataManager().getData(player.getUniqueId(), key);
        if (!optional.isPresent()) {
            message(this.sender, Message.PLAYERS_DATA_KEYS_EMPTY, "%player%", player.getName(), "%key%", key);
            return CommandType.DEFAULT;
        }

        Data data = optional.get();
        data.remove(value);

        message(this.sender, Message.PLAYERS_DATA_SUBTRACT, "%player%", player.getName(), "%key%", key);

        return CommandType.SUCCESS;
    }

}
