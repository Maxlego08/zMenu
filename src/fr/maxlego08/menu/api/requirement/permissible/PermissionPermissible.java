package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Permissible;

/**
 * Represents a condition where a player is required to have a specific permission.
 */
public interface PermissionPermissible extends Permissible {

    /**
     * Gets the permission that the player must have.
     *
     * @return The required permission.
     */
    String getPermission();

    /**
     * Checks if the condition is reversed, meaning the player should not have the specified permission.
     *
     * @return True if the condition is reversed; otherwise, false.
     */
    boolean isReverse();
}
