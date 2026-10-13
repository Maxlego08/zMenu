package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.SectionSerializable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of requirements that a player must meet to perform a certain action.
 */
public interface Requirement extends SectionSerializable {

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
    List<fr.maxlego08.menu.api.requirement.Action> getDenyActions();

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

    /**
     * Writes this requirement in the format the requirement loader reads. Values equal to their
     * default are left out.
     *
     * @param section The section to write into.
     */
    @Override
    default void serialize(@NotNull ConfigurationSection section) {
        List<Map<String, Object>> permissibles = new ArrayList<>(this.getRequirements().size());
        for (Permissible permissible : this.getRequirements()) {
            permissibles.add(permissible.serialize());
        }
        if (!permissibles.isEmpty()) section.set("requirements", permissibles);
        if (!this.getSuccessActions().isEmpty()) section.set("success", Permissible.serializeActions(this.getSuccessActions()));
        if (!this.getDenyActions().isEmpty()) section.set("deny", Permissible.serializeActions(this.getDenyActions()));

        if (!new HashSet<>(this.getClickTypes()).equals(new HashSet<>(Configuration.allClicksType))) {
            List<String> clicks = new ArrayList<>(this.getClickTypes().size());
            for (ClickType clickType : this.getClickTypes()) {
                clicks.add(clickType.name());
            }
            section.set("clicks", clicks);
        }
        if (this.getMinimumRequirements() != permissibles.size()) {
            section.set("minimum-requirement", this.getMinimumRequirements());
        }
    }
}
