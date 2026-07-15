package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

/**
 * /zmenu unlink - force-detach this server from the website: revoke the link on the site (API call)
 * and clear the local live-sync credential. The local data is cleared even if the API call fails.
 */
public class CommandMenuUnlink extends SubCommand<ZMenuPlugin> {

    public CommandMenuUnlink(ZMenuPlugin plugin) {
        super(plugin, "unlink", "ul");
        this.setPermission(Permission.ZMENU_UNLINK.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {

        this.plugin.getWebsiteManager().forceUnlink(commandDispatch.getSender());

        return CommandResultType.SUCCESS;
    }
}
