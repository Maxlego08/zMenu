package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The PermissibleButton interface manages permissions for buttons.
 */
public interface PermissibleButton {

    /**
     * Returns the button that will be used if the permission condition does not pass.
     *
     * @return The button to be used if the permission condition fails.
     */
    Button getElseButton();

    /**
     * Returns the parent button.
     *
     * @return The parent button.
     */
    Button getParentButton();

    /**
     * Returns the real parent button.
     *
     * @return The real parent button.
     */
    Button getMasterParentButton();

    /**
     * Returns a list of permissions that the player must have.
     *
     * @return The list of required permissions.
     */
    List<PermissionPermissible> getPermissions();

    /**
     * Returns a list of permissions. The player must have at least one permission from the list.
     *
     * @return The list of optional permissions.
     */
    List<PermissionPermissible> getOrPermission();

    /**
     * Checks if the player has the required permissions.
     *
     * @return `true` if the player has the required permissions, otherwise `false`.
     */
    boolean hasPermission();

    /**
     * Checks if there is an alternative button to display when the permission condition fails.
     *
     * @return `true` if there is an alternative button, otherwise `false`.
     */
    boolean hasElseButton();


    /**
     * Checks if the player has the required permission to interact with the button.
     *
     * @param player    The player who will be checked for permission.
     * @param inventory The inventory associated with the button.
     * @param placeholders The placeholders
     * @return `true` if the player has the required permission, otherwise `false`.
     */
    boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders);

}
