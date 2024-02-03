package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class ZJumpButton extends ZNextButton {
    private final int page;
    private final InventoryManager inventoryManager;

    /**
     * @param page             the real page(start from 0)
     * @param inventoryManager the inventory manager
     */
    public ZJumpButton(InventoryManager inventoryManager, int page) {
        super(inventoryManager);
        this.inventoryManager = inventoryManager;
        this.page = page;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onClick(player, event, inventory, slot);

        Inventory toInventory = inventory.getMenuInventory();
        this.inventoryManager.openInventory(player, toInventory, page, new ArrayList<>());
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory) {
        return this.page != inventory.getPage();
    }
}
