package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Permissible;

public interface PermissionPermissible extends Permissible {

    /**
     * The permission that the player will have to have
     *
     * @return permission
     */
	String getPermission();

    /**
     * Allows to check if the player does not have the permissions
     *
     * @return boolean
     */
	boolean isReverse();

}
