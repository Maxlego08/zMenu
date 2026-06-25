package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.website.WebsiteManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuLogin extends VCommand {

    public CommandMenuLogin(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_LOGIN);
        this.addSubCommand("login");
        this.setPermission(Permission.ZMENU_LOGIN);
        // Token is optional: with no argument we start the live-sync device flow (RFC 8628),
        // with a token argument we keep the legacy token-paste login.
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String token = this.argAsString(0, null);

        if (token == null || token.isEmpty()) {
            // No token pasted -> link this server for live sync via the device authorization flow.
            plugin.getWebsiteManager().getLiveSyncManager().startDeviceFlow(this.sender);
            return CommandType.SUCCESS;
        }

        WebsiteManager manager = plugin.getWebsiteManager();
        manager.login(this.sender, token);

        return CommandType.SUCCESS;
    }

}
