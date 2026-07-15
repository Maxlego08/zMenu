package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuPlayers extends SubCommand<ZMenuPlugin> {

    public CommandMenuPlayers(ZMenuPlugin plugin) {
        super(plugin, "players", "p");
        this.setPermission(Permission.ZMENU_PLAYERS.getPermission());
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
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        return CommandResultType.SUCCESS;
    }
}
