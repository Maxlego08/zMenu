package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.BackButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.buttons.ZButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class ZBackButton extends ZButton implements BackButton {

    protected final InventoryManager inventoryManager;
    protected Inventory inventory;

    public ZBackButton(InventoryManager inventoryManager) {
        super();
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        if (this.inventory == null) {
            return;
        }

        List<Inventory> oldInventories = inventory.getOldInventories();
        oldInventories.remove(this.inventory);
        Inventory toInventory = this.inventory;

        inventory.getButtons().forEach(button -> button.onBackClick(player, event, inventory, oldInventories, toInventory, slot));

        this.inventoryManager.openInventory(player, toInventory, 1, oldInventories);
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {

        List<Inventory> oldInventories = inventory.getOldInventories();
        if (!oldInventories.isEmpty()) {
            this.inventory = oldInventories.get(oldInventories.size() - 1);
        }
    }

}
