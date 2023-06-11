package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.List;
import java.util.Optional;

public class CommandInventory extends VCommand {

    private final Command command;

    /**
     * @param plugin Menu plugin
     * @param command Menu Command
     */
    public CommandInventory(MenuPlugin plugin, Command command) {
        super(plugin);
        this.command = command;
        this.setPermission(command.getPermission());
        this.setConsoleCanUse(false);

        if (command.hasArgument()) {

            this.setExtendedArgs(true);

            for (String argument : command.getArguments()) {
                this.addRequireArg(argument);
            }
        }
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        if (this.command.hasArgument()) {

            CommandManager commandManager = plugin.getCommandManager();

            List<String> arguments = this.command.getArguments();
            for (int index = 0; index != arguments.size(); index++) {

                StringBuilder value = new StringBuilder(this.args[index]);
                String argument = arguments.get(index);

                if (this.args.length > arguments.size() && index == arguments.size() - 1) {

                    value.append(" ");

                    for (int argIndex = index + 1; argIndex < this.args.length; argIndex++) {

                        String newArgument = this.args[argIndex];
                        value.append(newArgument);

                        if (argIndex < this.args.length - 1) {
                            value.append(" ");
                        }

                    }

                    commandManager.setPlayerArgument(this.player, argument, value.toString());

                } else {

                    commandManager.setPlayerArgument(this.player, argument, value.toString());

                }

            }

        }

        String inventoryName = this.command.getInventory();
        InventoryManager manager = plugin.getInventoryManager();
        Optional<Inventory> optional;

        if (inventoryName.contains(":")) {

            String[] values = inventoryName.split(":");
            optional = manager.getInventory(values[0], values[1]);

        } else {
            optional = manager.getInventory(inventoryName);
        }

        if (optional.isPresent()) {
            manager.openInventory(this.player, optional.get());
        } else {
            message(this.player, Message.INVENTORY_ERROR, "%name%", inventoryName);
        }

        return CommandType.SUCCESS;
    }

    public Command getCommand() {
        return this.command;
    }

}
