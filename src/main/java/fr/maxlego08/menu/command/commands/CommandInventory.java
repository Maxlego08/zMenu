package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.command.validators.OnlinePlayerArgumentValidator;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandInventory extends VCommand {

    private final Command command;

    /**
     * @param plugin  Menu plugin
     * @param command Menu Command
     */
    public CommandInventory(ZMenuPlugin plugin, Command command, boolean isSubCommands) {
        super(plugin);
        this.command = command;

        this.setPermission(command.permission());
        this.setDenyMessage(command.denyMessage());
        this.setConsoleCanUse(true);

        if (isSubCommands) {
            this.addSubCommand(command.command());
            this.addSubCommand(command.aliases());
        }

        if (command.hasArgument()) {

            this.setExtendedArgs(true);
            for (CommandArgument argument : command.arguments()) {
                if (argument.isRequired()) {
                    this.addRequireArg(argument.getArgument(), (a, b) -> argument.getAutoCompletion());
                } else {
                    this.addOptionalArg(argument.getArgument(), (a, b) -> argument.getAutoCompletion());
                }
            }
        }

        command.subCommands().forEach(subCommand -> this.addSubCommand(new CommandInventory(plugin, subCommand, true)));
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
    protected CommandType perform(ZMenuPlugin plugin) {

        String inventoryName = this.command.inventory();
        InventoryManager manager = plugin.getInventoryManager();
        Optional<Inventory> optional = getInventoryByName(inventoryName);
        CommandArgument lastArgument = null;
        Placeholders placeholders = new Placeholders();
        Map<String, String> playerArguments = new HashMap<>();
        Player targetPlayer = null;
        if (sender instanceof Player) {
            targetPlayer = this.player;
        }

        if (this.command.hasArgument()) {

            List<CommandArgument> arguments = this.command.arguments();
            for (int index = 0; index < arguments.size(); index++) {

                CommandArgument argument = arguments.get(index);
                lastArgument = argument;
                String defaultValue = argument.getDefaultValue();
                StringBuilder value = new StringBuilder(index < this.args.length ? this.args[index] : (defaultValue != null && !defaultValue.isEmpty() ? defaultValue : ""));
                if (value.isEmpty() && argument.isRequired()) {
                    return CommandType.SYNTAX_ERROR;
                }

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

                if (value.isEmpty() && !argument.isRequired()) {
                    lastArgument = null;
                } else {

                    String result = value.toString();
                    Optional<CommandArgumentValidator> validatorOptional = plugin.getCommandManager().getArgumentValidator(argument.getType());

                    if (validatorOptional.isPresent()) {
                        CommandArgumentValidator validator = validatorOptional.get();
                        if (targetPlayer == null && validator instanceof OnlinePlayerArgumentValidator){
                            targetPlayer = plugin.getServer().getPlayerExact(value.toString());
                        }

                        if (!validator.isValid(result)) {
                            message(this.plugin, sender, validator.getErrorMessage(), "%argument%", argument.getArgument());
                            return CommandType.DEFAULT;
                        }
                    }

                    placeholders.register(argument.getArgument(), result);
                    playerArguments.put(argument.getArgument(), value.toString());
                }
            }
        }

        if (targetPlayer == null){
            message(this.plugin, sender, Message.COMMAND_NO_CONSOLE);
            return CommandType.DEFAULT;
        }
        Player finalTargetPlayer = targetPlayer;

        if (!playerArguments.isEmpty()){
            CommandManager commandManager = plugin.getCommandManager();
            playerArguments.forEach((argument, value) ->
                    commandManager.setPlayerArgument(finalTargetPlayer, argument, value)
            );
        }

        boolean performMainActions = lastArgument == null || lastArgument.isPerformMainActions();

        InventoryDefault inventoryDefault = new InventoryDefault();
        inventoryDefault.setPlugin(plugin);

        if (lastArgument != null) {
            lastArgument.getActions().forEach(action -> action.preExecute(finalTargetPlayer, null, inventoryDefault, placeholders));
        }

        if (performMainActions) {
            this.command.actions().forEach(action -> action.preExecute(finalTargetPlayer, null, inventoryDefault, placeholders));
            optional.ifPresent(inventory -> manager.openInventory(finalTargetPlayer, inventory));
        }

        return CommandType.SUCCESS;
    }

    public Command getCommand() {
        return this.command;
    }
}
