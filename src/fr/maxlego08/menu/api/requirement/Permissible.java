package fr.maxlego08.menu.api.requirement;

import org.bukkit.entity.Player;

/**
 * Represents a condition that can be checked to determine if a player has permission.
 */
public interface Permissible {

    /**
     * Checks if the player has the required permission.
     *
     * @param player The current player.
     * @return True if the player has permission; otherwise, false.
     */
    boolean hasPermission(Player player);

    /**
     * Checks if the permissible is valid.
     * This method is used to ensure the integrity of the permissible condition.
     *
     * @return True if the permissible is valid; otherwise, false.
     */
    boolean isValid();
}
