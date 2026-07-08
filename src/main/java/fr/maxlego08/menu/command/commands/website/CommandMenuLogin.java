package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuLogin extends SubCommand<ZMenuPlugin> {

    public CommandMenuLogin(ZMenuPlugin plugin) {
        super(plugin, "login");
        this.setPermission(Permission.ZMENU_LOGIN.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        this.plugin.getWebsiteManager().startDeviceFlow(commandDispatch.getSender());
        return CommandResultType.SUCCESS;
    }
}
