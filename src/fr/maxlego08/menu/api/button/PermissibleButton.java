package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * <p>Manage permissions</p>
 */
public interface PermissibleButton {

    /**
     * Returns the button that will be used if the condition does not pass
     *
     * @return else button
     */
	Button getElseButton();

    /**
     * Returns the parent button
     *
     * @return button
     */
	Button getParentButton();

    /**
     * Return the real parent
     *
     * @return button
     */
	Button getMasterParentButton();

    /**
     * List of permissions that the player must have
     *
     * @return permissions
     */
    List<PermissionPermissible> getPermissions();

    /**
     * List of permissions, the player must have at least one permission
     *
     * @return permissions
     */
    List<PermissionPermissible> getOrPermission();

    /**
     * Allow to check if the permission is valid
     *
     * @return true
     */
	boolean hasPermission();

    /**
     * Allows to know if there is a button to display
     *
     * @return boolean
     */
	boolean hasElseButton();

    /**
     * Allows to check if the player has the permission
     *
     * @param player Player who will check the permission
     * @param inventory Inventory
     * @return boolean
     */
	boolean checkPermission(Player player, InventoryDefault inventory);

}
