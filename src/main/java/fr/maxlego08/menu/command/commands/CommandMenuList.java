package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuList extends VCommand {

    public CommandMenuList(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_LIST);
        this.setDescription(Message.DESCRIPTION_LIST);
        this.addSubCommand("list", "l");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        plugin.getInventoryManager().sendInventories(this.sender);
        return CommandType.SUCCESS;
    }

}
