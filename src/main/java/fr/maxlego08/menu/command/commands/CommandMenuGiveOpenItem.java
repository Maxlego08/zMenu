package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.stream.Collectors;

public class CommandMenuGiveOpenItem extends VCommand {

    public CommandMenuGiveOpenItem(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_OPEN_ITEM);
        this.setDescription(Message.DESCRIPTION_OPEN_ITEM);
        this.addSubCommand("giveopenitem");
        this.addSubCommand("goi");

        InventoryManager inventoryManager = plugin.getInventoryManager();
        this.addRequireArg("inventory name", (a, b) -> inventoryManager.getInventories().stream().map(e -> (e.getPlugin().getName() + ":" + e.getFileName()).toLowerCase()).collect(Collectors.toList()));
        this.addOptionalArg("player");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String inventoryName = this.argAsString(0);
        Player player = this.argAsPlayer(1, this.player);
        if (player == null) {
            message(plugin, this.sender, sender instanceof ConsoleCommandSender ? Message.INVENTORY_OPEN_ERROR_CONSOLE : Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandType.DEFAULT;
        }

        InventoryManager inventoryManager = plugin.getInventoryManager();
        Optional<Inventory> optional = findInventory(inventoryName, inventoryManager);

        if (optional.isEmpty()) {
            message(plugin, this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
            return CommandType.DEFAULT;
        }

        Inventory inventory = optional.get();
        if (inventory.getOpenWithItem() == null) {
            message(plugin, sender, Message.INVENTORY_OPEN_ITEM_ERROR, "%name%", inventoryName);
            return CommandType.DEFAULT;
        } else {
            ItemStack itemStack = inventory.getOpenWithItem().getItemStack().build(player);
            give(player, itemStack);
            message(plugin, sender, Message.INVENTORY_OPEN_ITEM_SUCCESS, "%name%", player.getName());
        }

        return CommandType.SUCCESS;
    }

}
