package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceholderButton extends PermissibleButton {

    private List<PlaceholderPermissible> placeholders = new ArrayList<>();

    /**
     * Retrieves the list of placeholders that must be met for this button to be visible.
     *
     * @return the list of placeholders that must be met for this button to be visible
     */
    public List<PlaceholderPermissible> getPlaceholders() {
        return this.placeholders;
    }

    /**
     * Sets the list of placeholders that must be met for this button to be visible.
     * This list is used in conjunction with the list of permissions returned by
     * {@link #getPermissions()}, and is used to specify alternative placeholders
     * that can be used to satisfy the visibility requirement of this button.
     *
     * @param placeholders the list of placeholders that must be met for this button to be visible
     */
    public void setPlaceholders(List<PlaceholderPermissible> placeholders) {
        this.placeholders = placeholders;
    }

    /**
     * Returns true if this button has any placeholders, false otherwise.
     *
     * @return true if this button has any placeholders, false otherwise
     */
    public boolean hasPlaceHolder() {
        return this.placeholders != null && !this.placeholders.isEmpty();
    }

    /**
     * Returns true if this button has any placeholders or permissions, false otherwise.
     * This method will first check if the button has any placeholders, and if so, it will return true.
     * If not, it will then check if the button has any permissions using the method from the superclass.
     *
     * @return true if this button has any placeholders or permissions, false otherwise
     */
    @Override
    public boolean hasPermission() {
        return this.hasPlaceHolder() || super.hasPermission();
    }

    /**
     * Checks if the player has permission to interact with this button.
     * Permissions are checked in the following order:
     * <ol>
     * <li>OR permissions (if any of the OR permissions are true, the method returns true)</li>
     * <li>AND permissions (if all of the AND permissions are true, the method returns true)</li>
     * <li>Placeholders (if all of the placeholders are true, the method returns true)</li>
     * </ol>
     * If none of the above conditions are true, the method returns false.
     *
     * @param player          the player to check
     * @param inventoryEngine the inventory engine
     * @param placeholders    the placeholders
     * @return true if the player has permission, false otherwise
     */
    @Override
    public boolean checkPermission(Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {
        // First check if player has permission
        if (!super.checkPermission(player, inventoryEngine, placeholders)) {
            return false;
        }

        if (this.placeholders.isEmpty()) return true;

        // Then we will check if the player to all valid placeholders
        for (PlaceholderPermissible placeholder : this.placeholders) {
            if (!placeholder.hasPermission(player, null, inventoryEngine, placeholders)) {
                return false;
            }
        }
        return true;
    }
}
