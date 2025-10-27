package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissibleButton extends PerformButton {

    private List<PermissionPermissible> permissions = new ArrayList<>();
    private List<PermissionPermissible> orPermissions = new ArrayList<>();
    private Button elseButton;
    private Button parentButton;

    /**
     * Retrieves the button displayed when the current button does not have the required permissions.
     *
     * @return the button displayed when the current button does not have the required permissions
     */
    public Button getElseButton() {
        return this.elseButton;
    }

    /**
     * Sets the button displayed when the current button does not have the required permissions.
     *
     * @param elseButton the button displayed when the current button does not have the required permissions
     */
    public void setElseButton(Button elseButton) {
        this.elseButton = elseButton;
    }

    /**
     * Returns true if this button has any permissions (either AND or OR).
     *
     * @return true if this button has any permissions, false otherwise
     */
    public boolean hasPermission() {
        return !this.permissions.isEmpty() || !this.orPermissions.isEmpty();
    }

    /**
     * Returns true if this button has an alternative button set using the "elseButton" method.
     *
     * @return true if this button has an alternative button set using the "elseButton" method, false otherwise
     */
    public boolean hasElseButton() {
        return this.elseButton != null;
    }

    /**
     * Checks if the player has permission to interact with this button.
     * Permissions are checked in the following order:
     * <ol>
     * <li>OR permissions (if any of the OR permissions are true, the method returns true)</li>
     * <li>AND permissions (if all of the AND permissions are true, the method returns true)</li>
     * </ol>
     * If none of the above conditions are true, the method returns false.
     *
     * @param player          the player to check
     * @param inventoryEngine the inventory engine
     * @param placeholders    the placeholders
     * @return true if the player has permission, false otherwise
     */
    public boolean checkPermission(Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {

        if (!this.orPermissions.isEmpty()) {
            for (PermissionPermissible permission : this.orPermissions) {
                if (permission.hasPermission(player, null, inventoryEngine, placeholders)) {
                    return true;
                }
            }
            return false;
        }

        if (!this.permissions.isEmpty()) {
            for (PermissionPermissible permission : this.permissions) {
                if (!permission.hasPermission(player, null, inventoryEngine, placeholders)) {
                    return false;
                }
            }
            return true;
        }

        return true;
    }

    /**
     * Retrieves the parent button of this button.
     * The parent button is the button that is closest to this button in the
     * button hierarchy.
     *
     * @return the parent button of this button, or null if this button does not have a parent
     */
    public Button getParentButton() {
        return this.parentButton;
    }

    /**
     * Sets the parent button of this button.
     * The parent button is the button that is closest to this button in the
     * button hierarchy.
     *
     * @param parentButton the parent button of this button
     */
    public void setParentButton(Button parentButton) {
        this.parentButton = parentButton;
    }

    /**
     * Retrieves the master parent button of this button.
     * The master parent button is the highest button in the button hierarchy that is not null.
     * If this button does not have a parent button, then this button is returned as the master parent button.
     *
     * @return the master parent button of this button
     */
    public Button getMasterParentButton() {
        Button button = this.getParentButton();
        return button == null ? (Button) this : button.getMasterParentButton();
    }

    /**
     * Retrieves the list of permissions that must be met for this button to be visible.
     * This list is used in conjunction with the list of permissions returned by
     * {@link #getPermissions()}, and is used to specify alternative permissions
     * that can be used to satisfy the visibility requirement of this button.
     *
     * @return the list of alternative permissions that can be used to satisfy the visibility requirement of this button
     */
    public List<PermissionPermissible> getOrPermission() {
        return this.orPermissions;
    }

    /**
     * Retrieves the list of permissions that must be met for this button to be visible.
     *
     * @return the list of permissions that must be met for this button to be visible
     */
    public List<PermissionPermissible> getPermissions() {
        return this.permissions;
    }

    /**
     * Sets the list of permissions that must be met for this button to be visible.
     * This list is used to specify the permissions that must be met for this button to be visible.
     * If this list is empty, then this button is always visible.
     *
     * @param permissions the list of permissions that must be met for this button to be visible
     */
    public void setPermissions(List<PermissionPermissible> permissions) {
        this.permissions = permissions;
    }

    /**
     * Sets the list of permissions that can be used to satisfy the visibility requirement of this button.
     * This list is used in conjunction with the list of permissions returned by
     * {@link #getPermissions()}, and is used to specify alternative permissions that can be used to satisfy the visibility requirement of this button.
     * If this list is empty, then the list of permissions returned by {@link #getPermissions()} is used exclusively to satisfy the visibility requirement of this button.
     *
     * @param orPermissions the list of permissions that can be used to satisfy the visibility requirement of this button
     */
    public void setOrPermissions(List<PermissionPermissible> orPermissions) {
        this.orPermissions = orPermissions;
    }
}
