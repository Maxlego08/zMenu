package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a set of requirements that a player must meet to perform a certain action.
 */
public interface Requirement {

    /**
     * Gets the minimum number of requirements that the player must fulfill for permission.
     * By default, the value will be the same as the total number of requirements.
     *
     * @return Minimum requirement.
     */
    int getMinimumRequirements();

    /**
     * Gets the list of permissibles that the player must check.
     *
     * @return List of permissibles.
     */
    @NotNull
    List<Permissible> getRequirements();

    /**
     * Gets the list of actions performed if the player doesn't have permission.
     *
     * @return List of deny actions.
     */
    @NotNull
    List<Action> getDenyActions();

    /**
     * Gets the list of actions performed if the player has permission.
     *
     * @return List of success actions.
     */
    @NotNull
    List<Action> getSuccessActions();

    /**
     * Executes the requirement. If the player has permission, the method will return true,
     * and the success actions will be executed. Otherwise, the denied actions will be executed.
     *
     * @param player       The player.
     * @param button       The Button can be nullable
     * @param inventory    The Inventory.
     * @param placeholders The placeholders.
     * @return True if the player has permission.
     */
    boolean execute(@NotNull Player player, @Nullable Button button,@NotNull InventoryEngine inventory,@NotNull Placeholders placeholders);

    /**
     * Gets the list of clicks that will be used for the requirement.
     *
     * @return List of ClickTypes.
     */
    @NotNull
    List<ClickType> getClickTypes();
}
