package fr.maxlego08.menu.api.requirement;

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
    void execute(Player player);
}
