package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

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
     * The permission that the player will have to have
     *
     * @return permission
     */
	String getPermission();

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
     * @param player
     * @param inventory
     * @return boolean
     */
	boolean checkPermission(Player player, InventoryDefault inventory);

    /**
     * Allows to check if the player does not have the permissions
     *
     * @return boolean
     */
	boolean isReverse();

}
