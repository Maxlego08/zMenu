package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class ZHomeButton extends ZBackButton implements HomeButton {

    public ZHomeButton(InventoryManager inventoryManager) {
        super(inventoryManager);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onClick(player, event, inventory, slot);

        if (this.inventory == null) {
            return;
        }

        Inventory toInventory = this.inventory;
        this.inventoryManager.openInventory(player, toInventory, 1, new ArrayList<>());

    }

    @Override
    public void onInventoryOpen(Player player, InventoryDefault inventory) {

        List<Inventory> oldInventories = inventory.getOldInventories();
        if (!oldInventories.isEmpty()) {
            this.inventory = oldInventories.get(0);
        }

    }

}
