package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuDisconnect extends VCommand {

    public CommandMenuDisconnect(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_DISCONNECT);
        this.addSubCommand("disconnect", "logout");
        this.setPermission(Permission.ZMENU_DESCRIPTION);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        WebsiteManager manager = plugin.getWebsiteManager();
        manager.disconnect(this.sender);

        return CommandType.SUCCESS;
    }

}
