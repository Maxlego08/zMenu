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

    public Button getElseButton() {
        return this.elseButton;
    }

    public void setElseButton(Button elseButton) {
        this.elseButton = elseButton;
    }

    public boolean hasPermission() {
        return !this.permissions.isEmpty() || !this.orPermissions.isEmpty();
    }

    public boolean hasElseButton() {
        return this.elseButton != null;
    }

    public boolean checkPermission(Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {

        if (!this.orPermissions.isEmpty()) {
            return this.orPermissions.stream().anyMatch(p -> p.hasPermission(player, null, inventoryEngine, placeholders));
        }

        if (!this.permissions.isEmpty()) {
            return this.permissions.stream().allMatch(p -> p.hasPermission(player, null, inventoryEngine, placeholders));
        }

        return true;
    }

    public Button getParentButton() {
        return this.parentButton;
    }

    public void setParentButton(Button parentButton) {
        this.parentButton = parentButton;
    }

    public Button getMasterParentButton() {
        Button button = this.getParentButton();
        return button == null ? (Button) this : button.getMasterParentButton();
    }

    public List<PermissionPermissible> getOrPermission() {
        return this.orPermissions;
    }

    public List<PermissionPermissible> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionPermissible> permissions) {
        this.permissions = permissions;
    }

    public void setOrPermissions(List<PermissionPermissible> orPermissions) {
        this.orPermissions = orPermissions;
    }
}
