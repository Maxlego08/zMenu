package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class RefreshInventoryAction extends Action {

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        if (button != null) {
            inventory.getPlugin().getInventoryManager().updateInventory(player);
        }
    }
}
