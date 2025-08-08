package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.Optional;
import java.util.stream.Collectors;

public class CommandMenuReloadCommand extends VCommand {

    public CommandMenuReloadCommand(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("command", "cmd");
        this.setPermission(Permission.ZMENU_RELOAD);
        this.addOptionalArg("command", (a, b) -> plugin.getCommandManager().getCommands().stream().map(e -> e.command().toLowerCase()).collect(Collectors.toList()));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String commandName = this.argAsString(0, null);
        CommandManager commandManager = plugin.getCommandManager();

        if (commandName != null) {
            Optional<Command> optional = commandManager.getCommand(commandName);

            if (optional.isEmpty()) {
                message(plugin, this.sender, Message.INVENTORY_OPEN_ERROR_COMMAND, "%name%", commandName);
                return CommandType.DEFAULT;
            }

            Command command = optional.get();
            plugin.getVInventoryManager().close(v -> {
                InventoryDefault inventoryDefault = (InventoryDefault) v;
                return !inventoryDefault.isClose() && inventoryDefault.getMenuInventory().getFileName().equals(command.inventory());
            });

            Message message = commandManager.reload(command) ? Message.RELOAD_COMMAND_FILE
                    : Message.RELOAD_COMMAND_ERROR;
            message(plugin, this.sender, message, "%name%", commandName);

            return CommandType.SUCCESS;
        }

        plugin.getVInventoryManager().close();

        commandManager.loadCommands();

        message(plugin, this.sender, Message.RELOAD_COMMAND);

        return CommandType.SUCCESS;
    }

}
