package fr.maxlego08.menu.command.commands.variables;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /zm variables <get|set|add|subtract|remove|keys> <server|global> ...}: the variables of
 * this server and the ones shared by every server. Player variables stay under {@code /zm players}.
 */
public class CommandMenuVariables extends SubCommand<ZMenuPlugin> {

    public CommandMenuVariables(ZMenuPlugin plugin) {
        super(plugin, "variables", "vars");
        this.setPermission(Permission.ZMENU_VARIABLES.getPermission());
        for (CommandMenuVariablesAction.Action action : CommandMenuVariablesAction.Action.values()) {
            this.addSubCommand(new CommandMenuVariablesAction(plugin, action));
        }
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        return CommandResultType.SUCCESS;
    }
}
