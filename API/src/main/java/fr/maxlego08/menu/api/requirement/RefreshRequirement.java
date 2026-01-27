package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a set of requirements that a player must meet to perform a certain action.
 *
 * @author Maxime "Maxlego08" L.
 */
public interface RefreshRequirement {

    /**
     * Checks if the requirement is a task.
     *
     * @return True if the requirement is a task.
     */
    boolean isTask();

    /**
     * Checks if the requirement should refresh the lore of the item.
     *
     * @return True if the requirement should refresh the lore of the item.
     */
    boolean isRefreshLore();

    /**
     * Checks if the requirement should refresh the name of the item.
     *
     * @return True if the requirement should refresh the name of the item.
     */
    boolean isRefreshName();

    /**
     * Checks if the requirement should refresh the button.
     *
     * @return True if the requirement should refresh the button.
     */
    boolean isRefreshButton();

    /**
     * Gets the update interval in seconds.
     *
     * @return The update interval in seconds.
     */
    int getUpdateInterval();

    /**
     * Gets the list of permissibles that the player must check.
     *
     * @return List of permissibles.
     */
    @NotNull
    List<Permissible> getRequirements();

    /**
     * Gets the list of permissibles that the player must check to enable the requirement.
     *
     * @return List of permissibles.
     */
    @NotNull
    List<Permissible> getEnableRequirements();

    /**
     * Checks if the requirement should refresh the item.
     *
     * @param player The player to check.
     * @param button The button to check.
     * @param inventoryEngine The inventory to check.
     * @param placeholders The placeholders to use.
     * @return True if the requirement should refresh the item.
     */
    boolean needRefresh(@NotNull Player player,@NotNull Button button,@NotNull InventoryEngine inventoryEngine,@NotNull Placeholders placeholders);

    /**
     * Checks if the requirement can be refreshed.
     *
     * @param player The player to check.
     * @param button The button to check.
     * @param inventoryEngine The inventory to check.
     * @param placeholders The placeholders to use.
     * @return True if the requirement can be refreshed.
     */
    boolean canRefresh(@NotNull Player player,@NotNull Button button,@NotNull InventoryEngine inventoryEngine,@NotNull Placeholders placeholders);
}
