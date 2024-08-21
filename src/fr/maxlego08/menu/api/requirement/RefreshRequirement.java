package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

import java.util.List;

public interface RefreshRequirement {

    boolean isTask();

    boolean isRefreshLore();

    boolean isRefreshName();

    boolean isRefreshButton();

    int getUpdateInterval();

    /**
     * Gets the list of permissibles that the player must check.
     *
     * @return List of permissibles.
     */
    List<Permissible> getRequirements();

    List<Permissible> getEnableRequirements();

    boolean needRefresh(Player player, Button button, InventoryDefault inventory, Placeholders placeholders);

    boolean canRefresh(Player player, Button button, InventoryDefault inventoryDefault, Placeholders placeholders);
}
