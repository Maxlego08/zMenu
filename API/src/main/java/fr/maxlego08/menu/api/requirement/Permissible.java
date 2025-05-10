package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a condition that can be checked to determine if a player has permission.
 */
public abstract class Permissible {

    private final List<Action> denyActions;
    private final List<Action> successActions;

    public Permissible(List<Action> denyActions, List<Action> successActions) {
        this.denyActions = denyActions;
        this.successActions = successActions;
    }

    /**
     * Checks if the player has permission to interact with the button in the given inventory and placeholders.
     * <p>
     * This method is called when a player attempts to interact with a button in an inventory.
     * <p>
     * If this method returns false, the button will not be interactable by the player and the deny actions will be performed.
     *
     * @param player          The player who is attempting to interact with the button.
     * @param button          The button that the player is attempting to interact with.
     * @param inventoryEngine The inventory that the button is in.
     * @param placeholders    The placeholders that are currently active in the inventory.
     * @return True if the player has permission; otherwise, false.
     */
    public abstract boolean hasPermission(Player player, Button button, InventoryEngine inventoryEngine, Placeholders placeholders);

    /**
     * Checks if the permissible is valid.
     * This method is used to ensure the integrity of the permissible condition.
     *
     * @return True if the permissible is valid; otherwise, false.
     */
    public abstract boolean isValid();

    /**
     * Gets the list of actions performed if the player doesn't have permission.
     *
     * @return List of deny actions.
     */
    public List<Action> getDenyActions() {
        return this.denyActions;
    }

    /**
     * Gets the list of actions performed if the player has permission.
     *
     * @return List of success actions.
     */
    public List<Action> getSuccessActions() {
        return this.successActions;
    }
}
