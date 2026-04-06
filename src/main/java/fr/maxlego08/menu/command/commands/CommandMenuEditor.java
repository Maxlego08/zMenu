package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuEditor extends VCommand {

    public CommandMenuEditor(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("editor");
        this.setDescription(Message.DESCRIPTION_EDITOR);
        this.setPermission(Permission.ZMENU_EDITOR);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        this.message(plugin, this.sender, "§fhttps://minecraft-inventory-builder.com/builder/");

        return CommandType.SUCCESS;
    }

}
