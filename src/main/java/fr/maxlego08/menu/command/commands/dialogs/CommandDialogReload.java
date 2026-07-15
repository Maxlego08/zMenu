package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandDialogReload extends SubCommand<ZMenuPlugin> {
    public CommandDialogReload(ZMenuPlugin plugin) {
        super(plugin, "reload", "rl");
        this.setPermission(Permission.ZMENU_RELOAD_DIALOG.getPermission());

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        DialogManager dialogManager = this.plugin.getDialogManager();
        dialogManager.reloadDialogs();

        MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.RELOAD_DIALOGS,"%dialogs%", dialogManager.getDialogs().size());
        return CommandResultType.SUCCESS;
    }
}
