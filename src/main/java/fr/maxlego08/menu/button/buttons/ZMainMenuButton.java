package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ZMainMenuButton extends ZHomeButton {
    private final InventoryManager inventoryManager;

    public ZMainMenuButton(InventoryManager inventoryManager) {
        super(inventoryManager);
        this.inventoryManager = inventoryManager;
    }


    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        inventoryManager.openInventory(player,Configuration.mainMenu);
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        return true;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }
}
