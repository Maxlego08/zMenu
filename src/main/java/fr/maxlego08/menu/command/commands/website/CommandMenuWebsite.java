package fr.maxlego08.menu.command.commands.website;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuWebsite extends SubCommand<ZMenuPlugin> {

    public CommandMenuWebsite(ZMenuPlugin plugin) {
        super(plugin, "website", "w");
        this.setPermission(Permission.ZMENU_WEBSITE.getPermission());

        this.addSubCommand(new CommandMenuDownload(plugin));
        this.addSubCommand(new CommandMenuLogin(plugin));
        this.addSubCommand(new CommandMenuConnect(plugin));
        this.addSubCommand(new CommandMenuUnlink(plugin));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        return CommandResultType.FAILURE;
    }
}
