package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandContributors extends SubCommand<ZMenuPlugin> {
    public CommandContributors(ZMenuPlugin plugin) {
        super(plugin, "contributors", "contrib");
        this.setPermission(Permission.ZMENU_CONTRIBUTORS.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        List<String> authors = this.plugin.getDescription().getAuthors();
        List<String> contributors = this.plugin.getDescription().getContributors();
        StringBuilder message = new StringBuilder();
        message.append("\n§6§l§nAuthors§r\n");
        for (String author : authors) {
            message.append("  §e- ").append(author).append("\n");
        }
        message.append("\n§b§l§nContributors§r\n");
        for (String contributor : contributors) {
            message.append("  §3- ").append(contributor).append("\n");
        }
        MessageUtils.message(this.plugin, commandDispatch.getSender(), message.toString());
        return CommandResultType.SUCCESS;
    }
}
