package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;

import java.util.Optional;

public class InventoryAction implements Action {

    private final InventoryManager inventoryManager;
    private final String inventory;
    private final String plugin;
    private final int page;

    public InventoryAction(InventoryManager inventoryManager, String inventory, String plugin, int page) {
        this.inventoryManager = inventoryManager;
        this.inventory = inventory;
        this.plugin = plugin;
        this.page = page;
    }

    @Override
    public void execute(Player player) {
        Optional<Inventory> optional = this.inventoryManager.getInventory(this.plugin, this.inventory);
        if (optional.isPresent()) {
            this.inventoryManager.openInventory(player, optional.get(), page);
        } else Logger.info("Unable to find the inventory " + inventory, Logger.LogType.WARNING);
    }

}
