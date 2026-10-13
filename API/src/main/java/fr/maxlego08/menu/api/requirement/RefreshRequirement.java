package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * Writes this refresh requirement in the format the refresh requirement loader reads.
     *
     * @param section The section to write into.
     */
    default void serialize(@NotNull ConfigurationSection section) {
        List<Map<String, Object>> requirements = new ArrayList<>(this.getRequirements().size());
        for (Permissible permissible : this.getRequirements()) {
            requirements.add(permissible.serialize());
        }
        List<Map<String, Object>> enableRequirements = new ArrayList<>(this.getEnableRequirements().size());
        for (Permissible permissible : this.getEnableRequirements()) {
            enableRequirements.add(permissible.serialize());
        }
        if (!requirements.isEmpty()) section.set("requirements", requirements);
        if (!enableRequirements.isEmpty()) section.set("enable-requirements", enableRequirements);
        if (this.isTask()) section.set("task", true);
        if (this.isRefreshLore()) section.set("refresh-lore", true);
        if (this.isRefreshName()) section.set("refresh-name", true);
        if (this.isRefreshButton()) section.set("refresh-button", true);
        if (this.getUpdateInterval() != 500) section.set("update-interval", this.getUpdateInterval());
    }
}
