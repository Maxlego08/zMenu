package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandMenuOpen extends VCommand {

    public CommandMenuOpen(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("open", "o");

        InventoryManager inventoryManager = plugin.getInventoryManager();
        this.addRequireArg("inventory name", (a, b) -> inventoryManager.getInventories().stream().map(e -> (e.getPlugin().getName() + ":" + e.getFileName()).toLowerCase()).collect(Collectors.toList()));

        this.addOptionalArg("player");
        this.addOptionalArg("display message", (a, b) -> Arrays.asList("false", "true"));
        this.addOptionalArg("args");
        this.setExtendedArgs(true);

        this.setDescription(Message.DESCRIPTION_OPEN);
        this.setPermission(Permission.ZMENU_OPEN);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        InventoryManager inventoryManager = plugin.getInventoryManager();

        String inventoryName = this.argAsString(0);
        Player player = this.argAsPlayer(1, this.player);
        boolean displayMessage = this.argAsBoolean(2, Config.enableOpenMessage);
        if (player == null) {
            message(this.sender, sender instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }

        Optional<Inventory> optional = findInventory(inventoryName, inventoryManager);

        if (!optional.isPresent()) {
            message(this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
            return CommandType.DEFAULT;
        }

        if (displayMessage) {
            if (this.sender == player) {
                message(this.sender, Message.INVENTORY_OPEN_SUCCESS, "%name%", inventoryName);
            } else {
                message(this.sender, Message.INVENTORY_OPEN_OTHER, "%name%", inventoryName, "%player%", player.getName());
            }
        }

        int page = 1;

        if (args.length >= 5) {
            CommandManager commandManager = plugin.getCommandManager();

            for (int i = 4; i < args.length; i++) {
                String name = String.valueOf(i - 4);
                StringBuilder value = new StringBuilder(args[i]);
                if (value.toString().contains(":")) {
                    String[] values = value.toString().split(":", 2);
                    name = values[0];
                    value = new StringBuilder(values[1]);
                    if (value.toString().startsWith("\"")) {
                        value = new StringBuilder(value.substring(value.indexOf("\"") + 1));
                        i++;
                        while (i < args.length && !args[i].endsWith("\"")) {
                            value.append(" ").append(args[i]);
                            i++;
                        }
                        if (i < args.length) {
                            value.append(" ").append(args[i], 0, args[i].lastIndexOf("\""));
                        }
                    }
                }

                if (name.equalsIgnoreCase("page")) {
                    try {
                        page = Integer.parseInt(value.toString());
                    } catch (Exception ignored) {
                    }
                }

                commandManager.setPlayerArgument(player, name, value.toString());
            }
        }

        Inventory inventory = optional.get();
        inventoryManager.openInventory(player, inventory, page);

        return CommandType.SUCCESS;
    }

}
