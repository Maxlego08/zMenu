package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.List;
import java.util.Optional;

public class CommandInventory extends VCommand {

    private final Command command;

    /**
     * @param plugin  Menu plugin
     * @param command Menu Command
     */
    public CommandInventory(MenuPlugin plugin, Command command) {
        super(plugin);
        this.command = command;
        this.setPermission(command.getPermission());
        this.setConsoleCanUse(false);

        if (command.hasArgument()) {

            this.setExtendedArgs(true);

            for (CommandArgument argument : command.getArguments()) {
                if (argument.isRequired()) {
                    this.addRequireArg(argument.getArgument(), (a, b) -> argument.getAutoCompletion());
                } else {
                    this.addOptionalArg(argument.getArgument(), (a, b) -> argument.getAutoCompletion());
                }
            }
        }
    }

    private Optional<Inventory> getInventoryByName(String inventoryName) {
        if (inventoryName == null) return Optional.empty();

        InventoryManager manager = this.plugin.getInventoryManager();
        if (inventoryName.contains(":")) {
            String[] values = inventoryName.split(":");
            return manager.getInventory(values[0], values[1]);
        } else return manager.getInventory(inventoryName);
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {

        String inventoryName = this.command.getInventory();
        InventoryManager manager = plugin.getInventoryManager();
        Optional<Inventory> optional = getInventoryByName(inventoryName);
        CommandArgument lastArgument = null;
        Placeholders placeholders = new Placeholders();

        if (this.command.hasArgument()) {

            CommandManager commandManager = plugin.getCommandManager();

            List<CommandArgument> arguments = this.command.getArguments();
            for (int index = 0; index != Math.min(arguments.size(), this.args.length); index++) {

                StringBuilder value = new StringBuilder(this.args[index]);
                CommandArgument argument = arguments.get(index);
                lastArgument = argument;

                if (this.args.length > arguments.size() && index == arguments.size() - 1) {

                    value.append(" ");

                    for (int argIndex = index + 1; argIndex < this.args.length; argIndex++) {
                        String newArgument = this.args[argIndex];
                        value.append(newArgument);

                        if (argIndex < this.args.length - 1) {
                            value.append(" ");
                        }
                    }
                }

                Optional<String> optionalInventory = argument.getInventory();
                if (optionalInventory.isPresent()) {
                    optional = getInventoryByName(optionalInventory.get());
                }

                placeholders.register(argument.getArgument(), value.toString());
                commandManager.setPlayerArgument(this.player, argument.getArgument(), value.toString());
            }
        }

        this.command.getActions().forEach(action -> action.preExecute(player, null, null, placeholders));
        if (lastArgument != null) {
            lastArgument.getActions().forEach(action -> action.preExecute(player, null, null, placeholders));
        }

        optional.ifPresent(inventory -> manager.openInventory(this.player, inventory));

        return CommandType.SUCCESS;
    }

    public Command getCommand() {
        return this.command;
    }

}
