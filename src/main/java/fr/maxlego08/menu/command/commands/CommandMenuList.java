package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuList extends SubCommand<ZMenuPlugin> {

    public CommandMenuList(ZMenuPlugin plugin) {
        super(plugin, "list", "l");
        this.setPermission(Permission.ZMENU_LIST.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        this.plugin.getInventoryManager().sendInventories(commandDispatch.getSender());
        return CommandResultType.SUCCESS;
    }
}
