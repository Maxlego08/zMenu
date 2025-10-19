package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceholderButton extends PermissibleButton {

    private List<PlaceholderPermissible> placeholders = new ArrayList<>();

    public List<PlaceholderPermissible> getPlaceholders() {
        return this.placeholders;
    }

    public void setPlaceholders(List<PlaceholderPermissible> placeholders) {
        this.placeholders = placeholders;
    }

    public boolean hasPlaceHolder() {
        return this.placeholders != null && !this.placeholders.isEmpty();
    }

    @Override
    public boolean hasPermission() {
        return this.hasPlaceHolder() || super.hasPermission();
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {
        // First check if player has permission
        if (!super.checkPermission(player, inventoryEngine, placeholders)) {
            return false;
        }

        // Then we will check if the player to all valid placeholders
        for (PlaceholderPermissible placeholder : this.placeholders) {
            if (!placeholder.hasPermission(player, null, inventoryEngine, placeholders)) {
                return false;
            }
        }
        return true;
    }
}
