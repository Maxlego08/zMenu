package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandMenuOpen extends VCommand {

    public CommandMenuOpen(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("open", "o");

        InventoryManager inventoryManager = plugin.getInventoryManager();
        this.addRequireArg("inventory name", (a, b) -> {
            List<String> inventories = new ArrayList<>();
            for (Inventory inventory : inventoryManager.getInventories()) {
                inventories.add((inventory.getPlugin().getName() + ":" + inventory.getFileName()).toLowerCase(Locale.ROOT));
            }
            return inventories;
        });

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
        boolean displayMessage = this.argAsBoolean(2, Configuration.enableOpenMessage);
        if (player == null) {
            this.message(plugin, this.sender, this.sender instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }

        Optional<Inventory> optional = this.findInventory(inventoryName, inventoryManager);

        if (optional.isEmpty()) {
            this.message(plugin, this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
            return CommandType.DEFAULT;
        }

        if (displayMessage) {
            if (this.sender == player) {
                this.message(plugin, this.sender, Message.INVENTORY_OPEN_SUCCESS, "%name%", inventoryName);
            } else {
                this.message(plugin, this.sender, Message.INVENTORY_OPEN_OTHER, "%name%", inventoryName, "%player%", player.getName());
            }
        }

        int page = 1;

        if (this.args.length >= 5) {
            CommandManager commandManager = plugin.getCommandManager();

            for (int i = 4; i < this.args.length; i++) {
                String name = String.valueOf(i - 4);
                StringBuilder value = new StringBuilder(this.args[i]);
                if (value.toString().contains(":")) {
                    String[] values = value.toString().split(":", 2);
                    name = values[0];
                    value = new StringBuilder(values[1]);
                    if (value.toString().startsWith("\"")) {
                        value = new StringBuilder(value.substring(value.indexOf("\"") + 1));
                        i++;
                        while (i < this.args.length && !this.args[i].endsWith("\"")) {
                            value.append(" ").append(this.args[i]);
                            i++;
                        }
                        if (i < this.args.length) {
                            value.append(" ").append(this.args[i], 0, this.args[i].lastIndexOf("\""));
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
