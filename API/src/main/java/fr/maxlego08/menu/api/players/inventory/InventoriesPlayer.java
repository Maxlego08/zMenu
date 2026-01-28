package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    void storeInventory(@NotNull Player player);

    /**
     * Allows giving the inventory back to the player
     *
     * @param player Player
     */
    void giveInventory(@NotNull Player player);

    void forceGiveInventory(@NotNull Player player);

    /**
     * Check if the player has an inventory saved
     *
     * @param uniqueId Player {@link UUID}
     * @return boolean
     */
    boolean hasSavedInventory(@NotNull UUID uniqueId);

    /**
     * Retrieve the player's inventory if it exists
     *
     * @param uniqueId Player {@link UUID}
     * @return optional
     */
    @NotNull
    Optional<InventoryPlayer> getPlayerInventory(@NotNull UUID uniqueId);

    @NotNull
    List<ItemStack> getInventory(@NotNull UUID uniqueId);

    void clearInventorie(@NotNull UUID uniqueId);

    void loadInventories();

    void restoreAllInventories();

}
