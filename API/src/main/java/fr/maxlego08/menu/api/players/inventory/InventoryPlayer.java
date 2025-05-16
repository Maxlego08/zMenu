package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;

import java.util.Map;

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
     * Allows giving the inventory back to the player
     *
     * @param player The player
     */
    void giveInventory(Player player);

    String toInventoryString();

    Map<Integer, String> getItems();
}
