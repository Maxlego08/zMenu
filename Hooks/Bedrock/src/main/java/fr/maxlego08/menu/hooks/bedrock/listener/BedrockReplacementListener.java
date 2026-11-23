package fr.maxlego08.menu.hooks.bedrock.listener;

import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.inventory.bedrock.BedrockInventory;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class BedrockReplacementListener implements Listener {

    private final BedrockManager bedrockManager;
    private final InventoryManager inventoryManager;

    public BedrockReplacementListener(BedrockManager bedrockManager, InventoryManager inventoryManager) {
        this.bedrockManager = bedrockManager;
        this.inventoryManager = inventoryManager;
    }

    @EventHandler
    public void onPlayerOpenInventory(PlayerOpenInventoryEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory instanceof BedrockInventory) {
            return;
        }
        if (this.bedrockManager.isBedrockPlayer(event.getPlayer())) {
            InventoryReplacement inventoryReplacement = inventory.getInventoryReplacement();
            if (inventoryReplacement != null) {
                switch (inventoryReplacement.mode()) {
                    case CUMULUS -> {
                        Optional<? extends BedrockInventory<?, ?, ?>> optionalBedrockInventory = this.bedrockManager.getBedrockInventory(inventoryReplacement.plugin(), inventoryReplacement.inventoryName());
                        if (optionalBedrockInventory.isEmpty()) {
                            return;
                        }
                        BedrockInventory<?, ?, ?> bedrockInventory = optionalBedrockInventory.get();
                        if (triggers(inventoryReplacement, event.getPage())) {
                            event.setCancelled(true);
                            this.bedrockManager.openBedrockInventory(event.getPlayer(), bedrockInventory, event.getOldInventories());
                        }
                    }
                    case CHEST -> {
                        Optional<Inventory> optionalInventory = this.inventoryManager.findInventory(inventoryReplacement.plugin() + ":" + inventoryReplacement.inventoryName());
                        if (optionalInventory.isEmpty()) {
                            return;
                        }
                        Inventory chestInventory = optionalInventory.get();
                        if (triggers(inventoryReplacement, event.getPage())) {
                            event.setCancelled(true);
                            this.inventoryManager.openInventory(event.getPlayer(), chestInventory, event.getPage(), event.getOldInventories());
                        }
                    }

                }
            }
        }
    }

    /**
     * The target was found from this replacement itself, so only its pages are checked:
     * {@link InventoryReplacement#shouldTrigger} compares a file key, which a path such as
     * {@code admin/shop} of {@code enable-inventory-paths} never matches.
     */
    private static boolean triggers(InventoryReplacement inventoryReplacement, int page) {
        return inventoryReplacement.pages().isEmpty() || inventoryReplacement.pages().contains(page);
    }
}
