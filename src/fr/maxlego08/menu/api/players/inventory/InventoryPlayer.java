package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;

public interface InventoryPlayer {

    /**
     * Saves the player's inventory to be stored
     *
     * @param player
     */
    void storeInventory(Player player);

    /**
     * Allows to give the inventory back to the player
     *
     * @param player
     */
    void giveInventory(Player player);

}
