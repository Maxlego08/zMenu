package fr.maxlego08.menu.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface MetaUpdater extends MessageSender {

    /**
     * Allows you to change the display name
     *
     * @param itemMeta - The item meta
     * @param text     - The text
     * @param player   - The player
     */
    void updateDisplayName(ItemMeta itemMeta, String text, Player player);

    /**
     * Allows you to change the lore
     *
     * @param itemMeta - The item meta
     * @param lore     - The lore
     * @param player   - The player
     */
    void updateLore(ItemMeta itemMeta, List<String> lore, Player player);

}
