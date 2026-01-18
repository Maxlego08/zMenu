package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.utils.EnumInventory;
import org.bukkit.entity.Player;

/**
 * Manager interface for Virtual Inventories.
 * Provides methods for creating inventories by type or ID for specific players and pages.
 */
public interface VInvManager {
    /**
     * Create an inventory for a player of a specified EnumInventory type and page.
     *
     * @param enumInventory The inventory type (enum).
     * @param player        The player for whom to create the inventory.
     * @param page          The page number to display.
     * @param objects       Additional context info.
     */
    void createInventory(EnumInventory enumInventory, Player player, int page, Object... objects);

    /**
     * Create an inventory for a player by numeric ID and page.
     *
     * @param id      The inventory identifier.
     * @param player  The player for whom to create the inventory.
     * @param page    The page number to display.
     * @param objects Additional context info.
     */
    void createInventory(int id, Player player, int page, Object... objects);
}
