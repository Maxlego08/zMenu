package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NonNull;

public class ZMainMenuButton extends ZHomeButton {
    private final InventoryManager inventoryManager;

    public ZMainMenuButton(InventoryManager inventoryManager) {
        super(inventoryManager);
        this.inventoryManager = inventoryManager;
    }


    @Override
    public void onClick(@NonNull Player player, @NonNull InventoryClickEvent event, @NonNull InventoryEngine inventory, int slot, @NonNull Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        inventoryManager.openInventory(player,Configuration.mainMenu);
    }

    @Override
    public boolean checkPermission(@NonNull Player player, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        return true;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }
}
