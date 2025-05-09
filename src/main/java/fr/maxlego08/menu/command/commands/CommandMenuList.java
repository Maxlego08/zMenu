package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuList extends VCommand {

    public CommandMenuList(MenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_LIST);
        this.setDescription(Message.DESCRIPTION_LIST);
        this.addSubCommand("list", "l");
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {
        plugin.getInventoryManager().sendInventories(this.sender);
        return CommandType.SUCCESS;
    }

}
