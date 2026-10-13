package fr.maxlego08.menu.command.commands.convert;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuConvert extends SubCommand<ZMenuPlugin> {

    public CommandMenuConvert(ZMenuPlugin plugin) {
        super(plugin, "convert");
        this.setPermission(Permission.ZMENU_CONVERT.getPermission());
        this.addSubCommand(new CommandMenuConvertDeluxeMenus(plugin));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "<white>Usage: <gray>/zm convert deluxemenus [menu]");
        return CommandResultType.SUCCESS;
    }
}
