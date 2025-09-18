package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>Management of player inventories</p>
 */
public interface InventoriesPlayer extends Listener {

    /**
     * Saves the player's inventory to be stored
     *
     * @param player Player
     */
    void storeInventory(Player player);

    /**
     * Allows giving the inventory back to the player
     *
     * @param player Player
     */
    void giveInventory(Player player);

    void forceGiveInventory(Player player);

    /**
     * Check if the player has an inventory saved
     *
     * @param uniqueId Player {@link UUID}
     * @return boolean
     */
    boolean hasSavedInventory(UUID uniqueId);

    /**
     * Retrieve the player's inventory if it exists
     *
     * @param uniqueId Player {@link UUID}
     * @return optional
     */
    Optional<InventoryPlayer> getPlayerInventory(UUID uniqueId);

    List<ItemStack> getInventory(UUID uniqueId);

    void clearInventorie(UUID uniqueId);

    void loadInventories();

}
