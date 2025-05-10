package fr.maxlego08.menu.button;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ZPermissibleButton extends ZPerformButton implements PermissibleButton {

    private List<PermissionPermissible> permissions = new ArrayList<>();
    private List<PermissionPermissible> orPermissions = new ArrayList<>();
    private Button elseButton;
    private Button parentButton;

    @Override
    public Button getElseButton() {
        return this.elseButton;
    }

    public ZPermissibleButton setElseButton(Button elseButton) {
        this.elseButton = elseButton;
        return this;
    }

    @Override
    public boolean hasPermission() {
        return !this.permissions.isEmpty() || !this.orPermissions.isEmpty();
    }

    @Override
    public boolean hasElseButton() {
        return this.elseButton != null;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventoryEngine, Placeholders placeholders) {

        if (!this.orPermissions.isEmpty()) {
            return this.orPermissions.stream().anyMatch(p -> p.hasPermission(player, null, inventoryEngine, placeholders));
        }

        if (!this.permissions.isEmpty()) {
            return this.permissions.stream().allMatch(p -> p.hasPermission(player, null, inventoryEngine, placeholders));
        }

        return true;
    }

    @Override
    public Button getParentButton() {
        return this.parentButton;
    }

    /**
     * @param parentButton The parent button
     * @return button
     */
    public ZPermissibleButton setParentButton(Button parentButton) {
        this.parentButton = parentButton;
        return this;
    }

    @Override
    public Button getMasterParentButton() {
        Button button = this.getParentButton();
        return button == null ? (Button) this : button.getMasterParentButton();
    }

    @Override
    public List<PermissionPermissible> getOrPermission() {
        return this.orPermissions;
    }

    @Override
    public List<PermissionPermissible> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionPermissible> permissions) {
        this.permissions = permissions;
    }

    public void setPermissions(List<String> permissions, String permission) {
        this.permissions = permissions.stream().map(ZPermissionPermissible::new).collect(Collectors.toList());
        if (permission != null) this.permissions.add(new ZPermissionPermissible(permission));
    }

    public void setOrPermissionsString(List<String> orPermissions) {
        this.orPermissions = orPermissions.stream().map(ZPermissionPermissible::new).collect(Collectors.toList());
    }

    public void setOrPermissions(List<PermissionPermissible> orPermissions) {
        this.orPermissions = orPermissions;
    }
}
