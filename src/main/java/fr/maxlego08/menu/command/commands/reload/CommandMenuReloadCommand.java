package fr.maxlego08.menu.command.commands.reload;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.command.Command;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class CommandMenuReloadCommand extends SubCommand<ZMenuPlugin> {

    public CommandMenuReloadCommand(ZMenuPlugin plugin) {
        super(plugin, "command", "cmd");
        this.setPermission(Permission.ZMENU_RELOAD_COMMAND.getPermission());
        this.addOptionalArgument(Commands.argument("command", StringArgumentType.string()).suggests((ctx, builder) -> {
            plugin.getCommandManager().getCommands().stream().filter(entry -> entry.command().toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                    .forEach(entry -> builder.suggest(entry.command()));
            return builder.buildFuture();
        }));

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        Optional<String> optionalCommand = commandDispatch.getOptionalArgument("command", String.class);
        CommandManager commandManager = this.plugin.getCommandManager();
        if (optionalCommand.isPresent()) {
            String commandName = optionalCommand.get();
            Optional<Command> optional = commandManager.getCommand(commandName);

            if (optional.isEmpty()) {
//                 this.message(plugin, this.sender, Message.INVENTORY_OPEN_ERROR_COMMAND, "%name%", commandName);
                return CommandResultType.SUCCESS;
            }

            Command command = optional.get();
            this.plugin.getVInventoryManager().close(v -> {
                InventoryDefault inventoryDefault = (InventoryDefault) v;
                return !inventoryDefault.isClose() && inventoryDefault.getMenuInventory().getFileName().equals(command.inventory());
            });

            Message message = commandManager.reload(command) ? Message.RELOAD_COMMAND_FILE
                    : Message.RELOAD_COMMAND_ERROR;
//             this.message(plugin, this.sender, message, "%name%", commandName);

            return CommandResultType.SUCCESS;
        }

        this.plugin.getVInventoryManager().close();
        commandManager.loadCommands();
//         this.message(plugin, this.sender, Message.RELOAD_COMMAND);

        return CommandResultType.SUCCESS;
    }
}
