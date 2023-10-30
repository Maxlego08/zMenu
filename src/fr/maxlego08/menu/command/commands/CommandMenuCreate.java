package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandMenuCreate extends VCommand {

    public CommandMenuCreate(MenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_CREATE);
        this.setDescription(Message.DESCRIPTION_CREATE);
        this.addSubCommand("create");
        this.addRequireArg("file name", (a, b) -> new ArrayList<>());
        this.addRequireArg("inventory size", (a, b) -> Arrays.asList("9", "18", "27", "36", "45", "54"));
        this.addRequireArg("inventory name", (a, b) -> new ArrayList<>());
        this.setExtendedArgs(true);
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        String fileName = this.argAsString(0);
        int inventorySize = this.argAsInteger(1);

        if (inventorySize % 9 != 0 || inventorySize < 9 || inventorySize > 54) {
            message(sender, Message.INVENTORY_CREATE_ERROR_SIZE);
            return CommandType.DEFAULT;
        }

        StringBuilder inventoryName = new StringBuilder();

        for (int i = 3; i < args.length; i++) {
            inventoryName.append(args[i]);
            if (i != args.length - 1) inventoryName.append(" ");
        }

        System.out.println(fileName + " - " + inventorySize + " - " + inventoryName);

        this.plugin.getInventoryManager().createNewInventory(this.sender, fileName, inventorySize, inventoryName.toString());

        return CommandType.SUCCESS;
    }

}
