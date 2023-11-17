package fr.maxlego08.menu.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * <p>Updates the display name and lore of a {@link ItemMeta}</p>
 * <p>Adds support for <a href="https://docs.advntr.dev/minimessage/index.html">MiniMessage</a> in ItemStacks and messages</p>
 */
public interface MetaUpdater extends MessageSender {

    /**
     * Updates the display name of the {@link ItemMeta}.
     *
     * @param itemMeta The ItemMeta to be updated.
     * @param text     The text to set as the display name.
     * @param player   The player for whom MiniMessage will be applied.
     */
    void updateDisplayName(ItemMeta itemMeta, String text, Player player);

    /**
     * Updates the lore of the {@link ItemMeta}.
     *
     * @param itemMeta The ItemMeta to be updated.
     * @param lore     The lore to set.
     * @param player   The player for whom MiniMessage will be applied.
     */
    void updateLore(ItemMeta itemMeta, List<String> lore, Player player);

    /**
     * Create an {@link Inventory}
     *
     * @param inventoryName   Inventory Name.
     * @param size            Inventory size.
     * @param inventoryHolder Inventory Holder.
     * @return Inventory with colored name
     */
    Inventory createInventory(String inventoryName, int size, InventoryHolder inventoryHolder);

}
