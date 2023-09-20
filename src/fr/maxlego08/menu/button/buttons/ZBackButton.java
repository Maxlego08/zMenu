package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.BackButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ZBackButton extends ZButton implements BackButton {

    protected final InventoryManager inventoryManager;
    protected Inventory inventory;

    /**
     * @param inventoryManager
     */
    public ZBackButton(InventoryManager inventoryManager) {
        super();
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
        super.onClick(player, event, inventory, slot);

        if (this.inventory == null) {
            return;
        }

        List<Inventory> oldInventories = inventory.getOldInventories();
        oldInventories.remove(this.inventory);

        Inventory toInventory = this.inventory;
        this.inventoryManager.openInventory(player, toInventory, 1, oldInventories);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryDefault inventory) {

        List<Inventory> oldInventories = inventory.getOldInventories();
        if (oldInventories.size() >= 1) {
            this.inventory = oldInventories.get(oldInventories.size() - 1);
        }

    }

}
