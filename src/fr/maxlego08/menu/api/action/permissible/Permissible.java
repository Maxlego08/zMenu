package fr.maxlego08.menu.api.action.permissible;

import org.bukkit.entity.Player;

public interface Permissible {

    /**
     * Check if player has permission
     *
     * @param player
     * @return boolean
     */
    boolean hasPermission(Player player);

}
