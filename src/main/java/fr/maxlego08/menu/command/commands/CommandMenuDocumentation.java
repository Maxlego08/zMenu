package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuDocumentation extends VCommand {

    public CommandMenuDocumentation(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("documentation");
        this.setPermission(Permission.ZMENU_DOCUMENTATION);
        this.setDescription(Message.DESCRIPTION_DOCUMENTATION);
        this.addOptionalArg("word");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String word = this.argAsString(0, null);
        if (word == null) {
            message(sender, Message.DOCUMENTATION_INFORMATION_LINK, "%link%", "https://docs.zmenu.dev/");
        } else {
            message(sender, Message.DOCUMENTATION_INFORMATION_LINK, "%link%", "https://docs.zmenu.dev/?q=" + word);
        }

        return CommandType.SUCCESS;
    }

}
