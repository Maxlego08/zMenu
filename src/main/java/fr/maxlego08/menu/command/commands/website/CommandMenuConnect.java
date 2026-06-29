package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuConnect extends VCommand {

    public CommandMenuConnect(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_CONNECT);
        this.addSubCommand("connect", "sync");
        this.setPermission(Permission.ZMENU_CONNECT);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        plugin.getWebsiteManager().connect(this.sender);

        return CommandType.SUCCESS;
    }

}
