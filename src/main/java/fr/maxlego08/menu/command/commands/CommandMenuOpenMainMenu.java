package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class CommandMenuOpenMainMenu extends VCommand {
    /**
     * @param plugin the plugin
     */
    public CommandMenuOpenMainMenu(ZMenuPlugin plugin) {
        super(plugin);

        this.addSubCommand("openMainMenu", "omm");
        this.addOptionalArg("player");
        this.addOptionalArg("display message", (a, b) -> Arrays.asList("false", "true"));
        this.setDescription(Message.DESCRIPTION_OPEN_MAIN_MENU);
        this.setPermission(Permission.ZMENU_OPEN);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        InventoryManager inventoryManager = plugin.getInventoryManager();

        Player player = this.argAsPlayer(1, this.player);
        boolean displayMessage = this.argAsBoolean(2, Config.enableOpenMessage);
        if (player == null) {
            message(plugin, this.sender, sender instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE
                    : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }

        String mainMenu = Config.mainMenu;

        Optional<Inventory> optional = inventoryManager.getInventory(mainMenu);

        if (optional.isEmpty()) {
            message(plugin, this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", mainMenu);
            return CommandType.DEFAULT;
        }

        if (displayMessage) {
            if (this.sender == player) {
                message(plugin, this.sender, Message.INVENTORY_OPEN_SUCCESS, "%name%", mainMenu);
            } else {
                message(plugin, this.sender, Message.INVENTORY_OPEN_OTHER, "%name%", mainMenu, "%player%",
                        player.getName());
            }
        }

        inventoryManager.openInventory(player, optional.get());

        return CommandType.SUCCESS;
    }
}
