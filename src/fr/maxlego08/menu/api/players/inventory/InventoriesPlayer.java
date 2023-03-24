package fr.maxlego08.menu.api.players.inventory;

import fr.maxlego08.menu.zcore.utils.storage.Saveable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.UUID;

public interface InventoriesPlayer extends Listener, Saveable {

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

    /**
     * Check if the player has an inventory saved
     *
     * @param uniqueId
     * @return boolean
     */
    boolean hasSavedInventory(UUID uniqueId);

    /**
     * Retrieve the player's inventory if it exists
     *
     * @param uniqueId
     * @return optional
     */
    Optional<InventoryPlayer> getPlayerInventory(UUID uniqueId);

}
