package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuDisconnect extends VCommand {

    public CommandMenuDisconnect(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_DISCONNECT);
        this.addSubCommand("disconnect", "logout");
        this.setPermission(Permission.ZMENU_DISCONNECT);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        // A live-sync link takes priority: /zmenu disconnect closes the live socket (kill switch).
        if (plugin.getWebsiteManager().getLiveSyncManager().isLinked()) {
            plugin.getWebsiteManager().getLiveSyncManager().disconnect(this.sender);
            return CommandType.SUCCESS;
        }

        WebsiteManager manager = plugin.getWebsiteManager();
        manager.disconnect(this.sender);

        return CommandType.SUCCESS;
    }

}
