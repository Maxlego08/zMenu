package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuConvert extends VCommand {

    public CommandMenuConvert(MenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_CONVERT);
        this.addSubCommand("convert");
        this.setDescription(Message.DESCRIPTION_CONVERT);
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        message(this.sender, Message.CONVERT_INFO);

        return CommandType.SUCCESS;
    }

}
