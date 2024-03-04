package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a condition that can be checked to determine if a player has permission.
 */
public interface Permissible {

    /**
     * Checks if the player has the required permission.
     *
     * @param player       The current player.
     * @param placeholders
     * @return True if the player has permission; otherwise, false.
     */
    boolean hasPermission(Player player, Button button, InventoryDefault inventory, Placeholders placeholders);

    /**
     * Checks if the permissible is valid.
     * This method is used to ensure the integrity of the permissible condition.
     *
     * @return True if the permissible is valid; otherwise, false.
     */
    boolean isValid();

    /**
     * Gets the list of actions performed if the player doesn't have permission.
     *
     * @return List of deny actions.
     */
    List<Action> getDenyActions();

    /**
     * Gets the list of actions performed if the player has permission.
     *
     * @return List of success actions.
     */
    List<Action> getSuccessActions();
}
