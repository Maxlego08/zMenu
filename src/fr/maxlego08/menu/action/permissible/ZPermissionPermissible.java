package fr.maxlego08.menu.action.permissible;

import fr.maxlego08.menu.api.action.permissible.PermissionPermissible;
import org.bukkit.entity.Player;

public class ZPermissionPermissible implements PermissionPermissible {

    private final String permission;
    private final boolean isReverse;

    /**
     * @param permission Permission
     * @param isReverse is reverse
     */
    public ZPermissionPermissible(String permission, boolean isReverse) {
        super();
        this.permission = permission;
        this.isReverse = isReverse;
    }

    @Override
    public boolean hasPermission(Player player) {
        return this.permission == null
                || (this.isReverse != player.hasPermission(this.permission));
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
