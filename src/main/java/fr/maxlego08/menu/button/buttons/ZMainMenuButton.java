package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.api.configuration.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ZMainMenuButton extends ZHomeButton {
    private final InventoryManager inventoryManager;

    /**
     * @param inventoryManager
     */
    public ZMainMenuButton(InventoryManager inventoryManager) {
        super(inventoryManager);
        this.inventoryManager = inventoryManager;
    }


    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        inventoryManager.openInventory(player, Config.mainMenu);
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        return true;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }
}
