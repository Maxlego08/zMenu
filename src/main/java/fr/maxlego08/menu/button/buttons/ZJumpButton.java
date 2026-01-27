package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public class ZJumpButton extends ZNextButton {
    private final int page;
    private final InventoryManager inventoryManager;

    public ZJumpButton(InventoryManager inventoryManager, int page) {
        super(inventoryManager);
        this.inventoryManager = inventoryManager;
        this.page = page;
    }

    @Override
    public void onClick(@NonNull Player player, @NonNull InventoryClickEvent event, @NonNull InventoryEngine inventory, int slot, @NonNull Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        Inventory toInventory = inventory.getMenuInventory();
        this.inventoryManager.openInventory(player, toInventory, page, new ArrayList<>());
    }

    @Override
    public boolean checkPermission(@NonNull Player player, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        return this.page != inventory.getPage();
    }
}
