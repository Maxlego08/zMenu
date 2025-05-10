package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuEditor extends VCommand {

    public CommandMenuEditor(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("editor");
        this.setDescription(Message.DESCRIPTION_EDITOR);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        message(plugin, this.sender, "§fhttps://minecraft-inventory-builder.com/builder/");

        return CommandType.SUCCESS;
    }

}
