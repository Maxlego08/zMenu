package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandMenuReloadInventory extends VCommand {

    public CommandMenuReloadInventory(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("inventory");
        this.setPermission(Permission.ZMENU_RELOAD);
        this.addOptionalArg("menu", (a, b) -> {
            List<String> menus = new ArrayList<>();
            for (Inventory inventory : plugin.getInventoryManager().getInventories()) {
                menus.add((inventory.getPlugin().getName() + ":" + inventory.getFileName()).toLowerCase());
            }
            return menus;
        });
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String inventoryName = this.argAsString(0, null);
        InventoryManager inventoryManager = plugin.getInventoryManager();

        if (inventoryName != null) {
            Optional<Inventory> optional;
            if (inventoryName.contains(":")) {
                String[] values = inventoryName.split(":");
                if (values.length == 2) {
                    optional = inventoryManager.getInventory(values[0], values[1]);
                } else {
                    optional = inventoryManager.getInventory(inventoryName);
                }
            } else {
                optional = inventoryManager.getInventory(inventoryName);
            }

            if (optional.isEmpty()) {
                message(plugin, this.sender, Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", inventoryName);
                return CommandType.DEFAULT;
            }

            Inventory inventory = optional.get();
            plugin.getVInventoryManager().close(v -> {
                InventoryDefault inventoryDefault = (InventoryDefault) v;
                return !inventoryDefault.isClose() && inventoryDefault.getMenuInventory().equals(inventory);
            });
            inventoryManager.reloadInventory(inventory);
            message(plugin, this.sender, Message.RELOAD_INVENTORY_FILE, "%name%", inventoryName);

            return CommandType.SUCCESS;
        }

        plugin.getVInventoryManager().close();

        inventoryManager.deleteInventories(plugin);
        inventoryManager.loadInventories();

        message(plugin, this.sender, Message.RELOAD_INVENTORY, "%inventories%", inventoryManager.getInventories(plugin).size());

        return CommandType.SUCCESS;
    }

}
