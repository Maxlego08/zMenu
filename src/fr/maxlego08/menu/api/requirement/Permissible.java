package fr.maxlego08.menu.api.requirement;

import org.bukkit.entity.Player;

public interface Permissible {

    /**
     * Check if player has permission
     *
     * @param player Current player
     * @return boolean
     */
    boolean hasPermission(Player player);

    /**
     * Check if permissible is valid
     *
     * @return boolean
     */
    boolean isValid();
}
