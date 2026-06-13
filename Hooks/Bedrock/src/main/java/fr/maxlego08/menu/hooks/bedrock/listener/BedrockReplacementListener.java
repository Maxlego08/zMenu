package fr.maxlego08.menu.hooks.bedrock.listener;

import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.inventory.bedrock.BedrockInventory;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class BedrockReplacementListener implements Listener {

    private final BedrockManager bedrockManager;

    public BedrockReplacementListener(BedrockManager bedrockManager) {
        this.bedrockManager = bedrockManager;
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
                Optional<BedrockInventory> optionalBedrockInventory = this.bedrockManager.getBedrockInventory(inventoryReplacement.plugin(), inventoryReplacement.inventoryName());
                if (optionalBedrockInventory.isEmpty()) {
                    return;
                }
                BedrockInventory bedrockInventory = optionalBedrockInventory.get();
                if (inventoryReplacement.shouldTrigger(bedrockInventory, event.getPage())) {
                    event.setCancelled(true);
                    this.bedrockManager.openBedrockInventory(event.getPlayer(), bedrockInventory);
                }
            }
        }
    }
}
