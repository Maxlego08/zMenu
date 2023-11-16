package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

/**
 * Represents an action that can be executed based on certain conditions.
 */
public interface Action {

    /**
     * Executes the action for the specified player.
     *
     * @param player The player who triggers the action.
     */
    void execute(Player player, Button button, InventoryDefault inventory);
}
