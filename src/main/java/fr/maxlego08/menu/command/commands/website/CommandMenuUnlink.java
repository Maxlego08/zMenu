package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

/**
 * /zmenu unlink - force-detach this server from the website: revoke the link on the site (API call)
 * and clear the local live-sync credential. The local data is cleared even if the API call fails.
 */
public class CommandMenuUnlink extends VCommand {

    public CommandMenuUnlink(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_UNLINK);
        this.addSubCommand("unlink");
        this.setPermission(Permission.ZMENU_UNLINK);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        plugin.getWebsiteManager().forceUnlink(this.sender);
        return CommandType.SUCCESS;
    }
}
