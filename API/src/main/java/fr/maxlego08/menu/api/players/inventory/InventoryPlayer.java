package fr.maxlego08.menu.api.players.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    void storeInventory(@NotNull Player player);

    /**
     * Allows giving the inventory back to the player
     *
     * @param player The player
     */
    void giveInventory(@NotNull Player player);

    void forceGiveInventory(@NotNull Player player);

    void setItems(@NotNull Map<Integer, ItemStack> items);

    void setItems(@NotNull List<ItemStack> items);

    void setItemsFromEncode(@NotNull Map<Integer, String> items);

    @NotNull
    String toInventoryString();

    @NotNull
    List<ItemStack> getItemStacks();

    @NotNull
    Map<Integer, String> getItems();

    boolean isPermanent();
}
