package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class ZPreviousButton extends ZButton implements HomeButton {

    private final InventoryManager inventoryManager;

    /**
     * @param inventoryManager the inventory manager
     */
    public ZPreviousButton(InventoryManager inventoryManager) {
        super();
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        if (inventory.getPage() != 1) {
            Inventory toInventory = inventory.getMenuInventory();
            this.inventoryManager.openInventory(player, toInventory, event.isLeftClick() ? inventory.getPage() - 1 : 1, new ArrayList<>());
        }
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        return inventory.getPage() != 1;
    }

}
