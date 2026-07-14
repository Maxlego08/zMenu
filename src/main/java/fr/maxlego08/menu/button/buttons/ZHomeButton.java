package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

// ToDo, vérifier le bon fonctionnement de cette class parce que je suis pas sur que c'est une bonne idée de stocker la variable pour tout le monde

public class ZHomeButton extends HomeButton {

    private final InventoryManager inventoryManager;
    protected Inventory inventory;

    public ZHomeButton(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void onClick(@NonNull Player player, @NonNull InventoryClickEvent event, @NonNull InventoryEngine inventory, int slot, @NonNull Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);

        if (this.inventory == null) {
            return;
        }

        Inventory toInventory = this.inventory;
        this.inventoryManager.openInventory(player, toInventory, 1, new ArrayList<>());
    }

    @Override
    public void onInventoryOpen(@NonNull Player player, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {

        List<Inventory> oldInventories = inventory.getOldInventories();
        if (!oldInventories.isEmpty()) {
            this.inventory = oldInventories.getFirst();
        }
    }

}
