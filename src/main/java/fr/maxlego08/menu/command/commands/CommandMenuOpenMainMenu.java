package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.enums.Message;
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
    public CommandMenuOpenMainMenu(MenuPlugin plugin) {
        super(plugin);

        this.addSubCommand("openMainMenu", "omm");
        this.addOptionalArg("player");
        this.addOptionalArg("display message", (a, b) -> Arrays.asList("false", "true"));
        this.setDescription(Message.DESCRIPTION_OPEN_MAIN_MENU);
        this.setPermission(Permission.ZMENU_OPEN);
    }

    @Override
    protected CommandType perform(MenuPlugin plugin) {
        InventoryManager inventoryManager = plugin.getInventoryManager();

        Player player = this.argAsPlayer(1, this.player);
        boolean displayMessage = this.argAsBoolean(2, Config.enableOpenMessage);
        if (player == null) {
            message(this.sender, sender instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE
                    : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }

        String mainMenu = Config.mainMenu;

        Optional<Inventory> optional = inventoryManager.getInventory(mainMenu);

        if (!optional.isPresent()) {
            message(this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", mainMenu);
            return CommandType.DEFAULT;
        }

        if (displayMessage) {
            if (this.sender == player) {
                message(this.sender, Message.INVENTORY_OPEN_SUCCESS, "%name%", mainMenu);
            } else {
                message(this.sender, Message.INVENTORY_OPEN_OTHER, "%name%", mainMenu, "%player%",
                        player.getName());
            }
        }

        inventoryManager.openInventory(player, optional.get());

        return CommandType.SUCCESS;
    }
}
