package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class ZNextButton extends ZButton implements HomeButton {

    private final InventoryManager inventoryManager;

    /**
     * @param inventoryManager
     */
    public ZNextButton(InventoryManager inventoryManager) {
        super();
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onClick(player, event, inventory, slot);
        if (inventory.getPage() != inventory.getMaxPage()) {
            Inventory toInventory = inventory.getMenuInventory();
            this.inventoryManager.openInventory(player, toInventory, inventory.getPage() + 1,
                    new ArrayList<>());
        }
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory) {
        return inventory.getPage() < inventory.getMaxPage();
    }

}
