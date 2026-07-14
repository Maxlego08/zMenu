package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuEditor extends SubCommand<ZMenuPlugin> {

    public CommandMenuEditor(ZMenuPlugin plugin) {
        super(plugin, "editor");
        this.setPermission(Permission.ZMENU_EDITOR.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "§fhttps://minecraft-inventory-builder.com/builder/");
        return CommandResultType.SUCCESS;
    }
}
