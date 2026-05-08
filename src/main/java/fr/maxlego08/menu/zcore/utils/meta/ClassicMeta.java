package fr.maxlego08.menu.zcore.utils.meta;

import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ClassicMeta extends ZUtils implements MetaUpdater {
    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, Player player) {
        itemMeta.setDisplayName(this.color(text));
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, OfflinePlayer offlineplayer) {
        itemMeta.setDisplayName(this.color(text));
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, Player player) {
        this.updateLore(itemMeta, lore, LoreType.PREPEND);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @NonNull OfflinePlayer offlineplayer) {
        this.updateLore(itemMeta, lore, LoreType.PREPEND);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @NonNull LoreType loreType) {

        List<String> newLore = new ArrayList<>();

        if (loreType == LoreType.PREPEND && itemMeta.hasLore()) {
            newLore.addAll(lore);
            newLore.addAll(itemMeta.getLore());
        } else if (loreType == LoreType.APPEND && itemMeta.hasLore()) {
            newLore.addAll(itemMeta.getLore());
            newLore.addAll(lore);
        } else {
            newLore.addAll(this.color(lore));
        }

        itemMeta.setLore(newLore);
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, int size, InventoryHolder inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, size, this.color(inventoryName));
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, @NonNull InventoryType inventoryType, InventoryHolder inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, inventoryType, this.color(inventoryName));
    }

    @Override
    public void sendTitle(@NonNull Player player, String title, @NonNull String subtitle, long start, long duration, long end) {
        player.sendTitle(this.color(this.papi(title, player, true)), this.color(this.papi(subtitle, player, true)), (int) start, (int) duration, (int) end);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull String message) {
        sender.sendMessage(this.color(message));
    }

    @Override
    public void openBook(@NonNull Player player, @NonNull String title, @NonNull String author, @NonNull List<String> lines) {
        player.sendMessage("§cYou cant open a book with your minecraft value !");
    }

    @Override
    public String getLegacyMessage(String message) {
        return this.color(message);
    }

    @Override
    public void sendAction(@NonNull Player player, @NonNull String message) {
        ActionBar.sendActionBar(player, message);
    }
}
