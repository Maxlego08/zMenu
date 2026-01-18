package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandDialogReload extends VCommand {
    public CommandDialogReload(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("reload","rl");
        this.setPermission(Permission.ZMENU_RELOAD_DIALOG);
        this.setDescription(Message.DESCRIPTION_DIALOGS_RELOAD);

    }
    @Override
    protected CommandType perform(ZMenuPlugin plugin){
        DialogManager dialogManager = plugin.getDialogManager();
        dialogManager.reloadDialogs();

        message(plugin, this.sender, Message.RELOAD_DIALOGS,"%dialogs%", dialogManager.getDialogs().size());
        return CommandType.SUCCESS;
    }
}
