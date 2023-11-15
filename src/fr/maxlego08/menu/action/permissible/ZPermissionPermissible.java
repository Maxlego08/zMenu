package fr.maxlego08.menu.action.permissible;

import fr.maxlego08.menu.api.action.permissible.PermissionPermissible;
import org.bukkit.entity.Player;

import java.util.Map;

public class ZPermissionPermissible implements PermissionPermissible {

    private final String permission;
    private final boolean isReverse;

    /**
     * @param permission Permission
     * @param isReverse  is reverse
     */
    public ZPermissionPermissible(String permission, boolean isReverse) {
        super();
        this.permission = permission;
        this.isReverse = isReverse;
    }

    public ZPermissionPermissible(Map<String, Object> map) {
        String permission = (String) map.getOrDefault("permission", null);
        boolean isReverse = permission != null && permission.startsWith("!");
        if (isReverse) permission = permission.substring(1);
        this.permission = permission;
        this.isReverse = isReverse;
    }

    @Override
    public boolean hasPermission(Player player) {
        return this.isReverse != player.hasPermission(this.permission);
    }

    @Override
    public boolean isValid() {
        return this.permission != null;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean isReverse() {
        return this.isReverse;
    }

}
