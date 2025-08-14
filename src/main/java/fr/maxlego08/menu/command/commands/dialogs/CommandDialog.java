package fr.maxlego08.menu.command.commands.dialogs;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandDialog extends VCommand {

    public CommandDialog(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("dialog", "d");
        this.setDescription(Message.DESCRIPTION_DIALOGS);
        this.addSubCommand(new CommandDialogOpen(plugin));
        this.addSubCommand(new CommandDialogReload(plugin));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        sendSyntax();
        return CommandType.SUCCESS;
    }
}
