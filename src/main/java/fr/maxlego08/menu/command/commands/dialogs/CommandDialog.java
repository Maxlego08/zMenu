package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandDialog extends SubCommand<ZMenuPlugin> {

    public CommandDialog(ZMenuPlugin plugin) {
        super(plugin, "dialogs", "dialog", "d");
        this.setPermission(Permission.ZMENU_DIALOG.getPermission());
        this.addSubCommand(new CommandDialogOpen(plugin));
        this.addSubCommand(new CommandDialogReload(plugin));
        this.addSubCommand(new CommandDialogOpenConfig(plugin));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        return CommandResultType.FAILURE;
    }
}
