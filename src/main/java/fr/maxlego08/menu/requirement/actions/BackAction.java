package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;

import java.util.List;

public class BackAction extends Action {

    private final InventoryManager inventoryManager;

    public BackAction(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        List<Inventory> oldInventories = inventory.getOldInventories();

        if (!oldInventories.isEmpty()) {
            Inventory currentInventory = oldInventories.get(oldInventories.size() - 1);
            oldInventories.remove(currentInventory);

            inventory.getButtons().forEach(btn -> btn.onBackClick(player, null, inventory, oldInventories, currentInventory, 0));
            this.inventoryManager.openInventory(player, currentInventory, 1, oldInventories);
        }
    }

}
