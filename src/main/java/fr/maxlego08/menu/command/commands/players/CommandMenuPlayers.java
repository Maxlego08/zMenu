package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuPlayers extends VCommand {

    public CommandMenuPlayers(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_PLAYERS);
        this.setDescription(Message.DESCRIPTION_PLAYERS);
        this.addSubCommand("players");
        this.addSubCommand(new CommandMenuPlayersSet(plugin));
        this.addSubCommand(new CommandMenuPlayersGet(plugin));
        this.addSubCommand(new CommandMenuPlayersRemove(plugin));
        this.addSubCommand(new CommandMenuPlayersKeys(plugin));
        this.addSubCommand(new CommandMenuPlayersClearAll(plugin));
        this.addSubCommand(new CommandMenuPlayersClearPlayer(plugin));
        this.addSubCommand(new CommandMenuPlayersSubtract(plugin));
        this.addSubCommand(new CommandMenuPlayersAdd(plugin));
        this.addSubCommand(new CommandMenuPlayersRemoveAll(plugin));
        this.addSubCommand(new CommandMenuPlayersConvert(plugin));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        sendSyntax();
        return CommandType.SUCCESS;
    }

}
