package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuConnect extends SubCommand<ZMenuPlugin> {

    public CommandMenuConnect(ZMenuPlugin plugin) {
        super(plugin, "connect", "sync");
        this.setPermission(Permission.ZMENU_CONNECT.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {

        this.plugin.getWebsiteManager().connect(commandDispatch.getSender());

        return CommandResultType.SUCCESS;
    }
}
