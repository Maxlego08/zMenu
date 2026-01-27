package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuDocumentation extends VCommand {

    public CommandMenuDocumentation(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("documentation");
        this.setPermission(Permission.ZMENU_DOCUMENTATION);
        this.setDescription(Message.DESCRIPTION_DOCUMENTATION);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        message(plugin, sender, Message.DOCUMENTATION_INFORMATION_LINK, "%link%", "https://docs.groupez.dev/zmenu/getting-started");
        return CommandType.SUCCESS;
    }

}
