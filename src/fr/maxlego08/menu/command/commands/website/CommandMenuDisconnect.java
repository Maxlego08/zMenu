package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuDisconnect extends VCommand {

    public CommandMenuDisconnect(MenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_DISCONNECT);
        this.addSubCommand("disconnect", "logout");
        this.setPermission(Permission.ZMENU_DESCRIPTION);
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        WebsiteManager manager = plugin.getWebsiteManager();
        manager.disconnect(this.sender);

        return CommandType.SUCCESS;
    }

}
