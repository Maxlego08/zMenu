package fr.maxlego08.menu.zcore.utils.meta;

import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ClassicMeta extends ZUtils implements MetaUpdater {
    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, Player player) {
        itemMeta.setDisplayName(color(text));
    }

    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, OfflinePlayer offlineplayer) {
        itemMeta.setDisplayName(color(text));
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, Player player) {
        updateLore(itemMeta, lore, LoreType.PREPEND);
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, OfflinePlayer offlineplayer) {
        updateLore(itemMeta, lore, LoreType.PREPEND);
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, LoreType loreType) {

        List<String> newLore = new ArrayList<>();

        if (loreType == LoreType.PREPEND && itemMeta.hasLore()) {
            newLore.addAll(lore);
            newLore.addAll(itemMeta.getLore());
        } else if (loreType == LoreType.APPEND && itemMeta.hasLore()) {
            newLore.addAll(itemMeta.getLore());
            newLore.addAll(lore);
        } else {
            newLore.addAll(color(lore));
        }

        itemMeta.setLore(newLore);
    }

    @Override
    public Inventory createInventory(String inventoryName, int size, InventoryHolder inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, size, color(inventoryName));
    }

    @Override
    public Inventory createInventory(String inventoryName, InventoryType inventoryType, InventoryHolder inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, inventoryType, color(inventoryName));
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, long start, long duration, long end) {
        player.sendTitle(color(papi(title, player, true)), color(papi(subtitle, player, true)), (int) start, (int) duration, (int) end);
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    @Override
    public void openBook(Player player, String title, String author, List<String> lines) {
        player.sendMessage("§cYou cant open a book with your minecraft version !");
    }

    @Override
    public void sendAction(Player player, String message) {
        ActionBar.sendActionBar(player, message);
    }
}
