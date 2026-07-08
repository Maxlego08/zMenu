package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CommandMenuOpen extends SubCommand<ZMenuPlugin> {
    private final InventoryManager inventoryManager;

    public CommandMenuOpen(ZMenuPlugin plugin) {
        super(plugin, "open", "o");
        this.setPermission(Permission.ZMENU_OPEN.getPermission());
        this.inventoryManager = plugin.getInventoryManager();

        this.addRequiredArgument(Commands.argument("inventory-name", StringArgumentType.string()).suggests((ctx, builder) -> {
            this.inventoryManager.getInventoryNames().stream().filter(entry -> entry.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        }));

        this.addOptionalArgument(Commands.argument("player", ArgumentTypes.player()));
        this.addOptionalArgument(Commands.argument("display-message", BoolArgumentType.bool()));
        this.addOptionalArgument(Commands.argument("args", StringArgumentType.greedyString()));

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        String inventoryName = commandDispatch.getArgument("inventory-name", String.class);

        Player player = commandDispatch.getOptionalArgument("player", Player.class).orElse(commandDispatch.getPlayer());
        if (player == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), commandDispatch.getSender() instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandResultType.SUCCESS;
        }

        Optional<Inventory> optionalInventory = this.inventoryManager.findInventory(inventoryName);
        if (optionalInventory.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
            return CommandResultType.SUCCESS;
        }
        Boolean displayMessage = commandDispatch.getArgument("display-message", Boolean.class, Configuration.enableOpenMessage);
        if (displayMessage) {
            if (commandDispatch.getSender() == player) {
                MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_SUCCESS, "%name%", inventoryName);
            } else {
                MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_OTHER, "%name%", inventoryName, "%player%", player.getName());
            }
        }

        int page = 1;

        Optional<String> remaining = commandDispatch.getOptionalArgument("args", String.class);

        if (remaining.isPresent()) {
            CommandManager commandManager = this.plugin.getCommandManager();

            List<String> args = splitArguments(remaining.get());

            int i = 0;
            for (String arg : args) {

                String name;
                String value;

                int index = arg.indexOf(':');

                if (index == -1) {
                    name = String.valueOf(i);
                    value = arg;
                } else {
                    name = arg.substring(0, index);
                    value = arg.substring(index + 1);
                }

                if (name.equalsIgnoreCase("page")) {
                    try {
                        page = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }
                }

                i++;
                commandManager.setPlayerArgument(player, name, value);
            }
        }

        this.inventoryManager.openInventory(player, optionalInventory.get(), page);

        return CommandResultType.SUCCESS;
    }

    private static List<String> splitArguments(String input) {

        List<String> result = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                quoted = !quoted;
                continue;
            }

            if (c == ' ' && !quoted) {
                if (!current.isEmpty()) {
                    result.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            result.add(current.toString());
        }

        return result;
    }
}
