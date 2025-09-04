package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.List;

public class CommandContributors extends VCommand {
    public CommandContributors(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("contributors", "contrib");
        this.setDescription(Message.DESCRIPTION_CONTRIBUTORS);
        this.setPermission(Permission.ZMENU_CONTRIBUTORS);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        List<String> authors = plugin.getDescription().getAuthors();
        List<String> contributors = plugin.getDescription().getContributors();
        StringBuilder message = new StringBuilder();
        message.append("\n§6§l§nAuthors§r\n");
        for (String author : authors) {
            message.append("  §e- ").append(author).append("\n");
        }
        message.append("\n§b§l§nContributors§r\n");
        for (String contributor : contributors) {
            message.append("  §3- ").append(contributor).append("\n");
        }
        message(plugin, this.sender, message.toString());
        return CommandType.SUCCESS;
    }
}
