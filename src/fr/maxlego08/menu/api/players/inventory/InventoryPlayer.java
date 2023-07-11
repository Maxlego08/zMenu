package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;

/**
 * <p>Player's {@link org.bukkit.inventory.Inventory}</p>
 */
public interface InventoryPlayer {

    /**
     * Saves the player's inventory to be stored
     *
     * @param player The player
     */
    void storeInventory(Player player);

    /**
     * Allows to give the inventory back to the player
     *
     * @param player The player
     */
    void giveInventory(Player player);

}
