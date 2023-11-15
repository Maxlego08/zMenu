package fr.maxlego08.menu.button;

import fr.maxlego08.menu.api.action.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
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
        return super.hasPermission() || this.hasPlaceHolder();
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory) {
        if (!this.hasPlaceHolder()) {

            return super.checkPermission(player, inventory);

        } else {

            // First check if player has permission
            if (!super.checkPermission(player, inventory)) {
                return false;
            }

            return this.placeholders.stream().allMatch(placeholder -> placeholder.hasPermission(player));
        }
    }
}
