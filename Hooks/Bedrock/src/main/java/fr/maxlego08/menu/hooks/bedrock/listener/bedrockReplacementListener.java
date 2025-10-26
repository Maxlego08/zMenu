package fr.maxlego08.menu.hooks.bedrock.listener;

import fr.maxlego08.menu.api.BedrockInventory;
import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class bedrockReplacementListener implements Listener {

    private final BedrockManager bedrockManager;

    public bedrockReplacementListener(BedrockManager bedrockManager) {
        this.bedrockManager = bedrockManager;
    }

    @EventHandler
    public void onPlayerOpenInventory(PlayerOpenInventoryEvent event) {
        if (event.getInventory() instanceof BedrockInventory) {
            return;
        }

        for (BedrockInventory inventory : this.bedrockManager.getBedrockInventory()) {
            InventoryReplacement replacement = inventory.getInventoryReplacement();
            if (replacement == null) {
                continue;
            }
            if (replacement.shouldTrigger(event)) {
                event.setCancelled(true);
                this.bedrockManager.openBedrockInventory(event.getPlayer(), inventory);
                return;
            }
        }
    }
}
