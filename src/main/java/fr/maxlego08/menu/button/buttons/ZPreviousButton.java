package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.buttons.ZButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ZPreviousButton extends ZButton implements HomeButton {

    private final InventoryManager inventoryManager;

    public ZPreviousButton(InventoryManager inventoryManager) {
        super();
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        if (inventory.getPage() != 1) {
            Inventory toInventory = inventory.getMenuInventory();
            this.inventoryManager.openInventory(player, toInventory, event.isLeftClick() ? inventory.getPage() - 1 : 1, inventory.getOldInventories());
        }
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        return inventory.getPage() != 1;
    }
}
