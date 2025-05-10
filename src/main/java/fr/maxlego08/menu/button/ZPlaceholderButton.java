package fr.maxlego08.menu.button;

import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class ZPlaceholderButton extends ZPermissibleButton implements PlaceholderButton {

    private List<PlaceholderPermissible> placeholders = new ArrayList<>();

    @Override
    public List<PlaceholderPermissible> getPlaceholders() {
        return this.placeholders;
    }

    public void setPlaceholders(List<PlaceholderPermissible> placeholders) {
        this.placeholders = placeholders;
    }

    @Override
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
        return this.placeholders.stream().allMatch(placeholder -> placeholder.hasPermission(player, null, inventoryEngine, placeholders));
    }
}
