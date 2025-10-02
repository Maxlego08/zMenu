package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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

    void forceGiveInventory(Player player);

    void setItems(Map<Integer, ItemStack> items);

    void setItems(List<ItemStack> items);

    void setItemsFromEncode(Map<Integer, String> items);

    String toInventoryString();

    List<ItemStack> getItemStacks();

    Map<Integer, String> getItems();
}
