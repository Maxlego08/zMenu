package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Implementation of the {@link PermissionPermissible} interface that checks player permissions
 * based on a specific permission node and an optional reverse flag.
 */
public class ZPermissionPermissible implements PermissionPermissible {

    private final String permission;
    private final boolean isReverse;

    /**
     * Constructs a ZPermissionPermissible with the specified permission node.
     *
     * @param permission The permission node to check.
     */
    public ZPermissionPermissible(String permission) {
        super();
        boolean isReverse = permission != null && permission.startsWith("!");
        if (isReverse) permission = permission.substring(1);
        this.permission = permission;
        this.isReverse = isReverse;
    }

    /**
     * Checks whether the player has the necessary permission based on the specified permission node and reverse flag.
     *
     * @param player The player whose permission is being checked.
     * @return {@code true} if the player has the necessary permission, otherwise {@code false}.
     */
    @Override
    public boolean hasPermission(Player player) {
        return this.isReverse != player.hasPermission(this.permission);
    }

    /**
     * Checks whether the ZPermissionPermissible instance is valid.
     *
     * @return {@code true} if the instance is valid, otherwise {@code false}.
     */
    @Override
    public boolean isValid() {
        if (this.permission == null) Logger.info("Permission is null !", Logger.LogType.WARNING);
        return this.permission != null;
    }

    /**
     * Gets the permission node associated with this permissible.
     *
     * @return The permission node.
     */
    @Override
    public String getPermission() {
        return this.permission;
    }

    /**
     * Checks whether the permission check should be reversed.
     *
     * @return {@code true} if the permission check should be reversed, otherwise {@code false}.
     */
    @Override
    public boolean isReverse() {
        return this.isReverse;
    }

}
