package fr.maxlego08.menu.api.requirement;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public interface Requirement {

    /**
     * The minimum number of requirements that the player must validate for permission, by default the value will be the same as the number of requirements
     *
     * @return minimum requirement
     */
    int getMinimumRequirements();

    /**
     * List of permissibles that the player will have to check
     *
     * @return permissibles
     */
    List<Permissible> getRequirements();

    /**
     * List of actions performed if the player doesn't have permission
     *
     * @return actions
     */
    List<Action> getDenyActions();

    /**
     * List of actions performed if the player have permission
     *
     * @return actions
     */
    List<Action> getSuccessActions();

    /**
     * Allows to execute the requirement, if the player to the permission then the method will return true, and the success actions will be executed, otherwise its will be denied actions
     *
     * @param player The player
     * @return true if player has permission
     */
    boolean execute(Player player);

    /**
     * List of clicks that will be used for the requirement
     *
     * @return clicks
     */
    List<ClickType> getClickTypes();

}
