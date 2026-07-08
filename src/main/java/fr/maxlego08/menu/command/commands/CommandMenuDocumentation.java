package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuDocumentation extends SubCommand<ZMenuPlugin> {

    public CommandMenuDocumentation(ZMenuPlugin plugin) {
        super(plugin, "documentation", "doc");
        this.setPermission(Permission.ZMENU_DOCUMENTATION.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.DOCUMENTATION_INFORMATION_LINK, "%link%", "https://docs.groupez.dev/zmenu/getting-started");
        return CommandResultType.SUCCESS;
    }
}
