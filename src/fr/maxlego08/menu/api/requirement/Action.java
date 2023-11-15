package fr.maxlego08.menu.api.requirement;

import org.bukkit.entity.Player;

public interface Action {

    /**
     * Allows to execute the action
     *
     * @param player Who execute the action
     */
    void execute(Player player);

}
