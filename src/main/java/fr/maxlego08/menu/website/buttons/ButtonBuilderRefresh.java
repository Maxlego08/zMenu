package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.website.ZWebsiteManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ButtonBuilderRefresh extends Button {

    private final ZMenuPlugin plugin;
    private boolean canUse = true;

    public ButtonBuilderRefresh(Plugin plugin) {
        this.plugin = (ZMenuPlugin) plugin;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onClick(@NonNull Player player, @NonNull InventoryClickEvent event, @NonNull InventoryEngine inventory, int slot, @NonNull Placeholders placeholders) {

        if (!this.canUse) return;
        this.canUse = false;

        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        plugin.getMetaUpdater().updateLore(meta, List.of("&cPlease wait"), LoreType.APPEND);
        itemStack.setItemMeta(meta);
        inventory.getSpigotInventory().setItem(slot, itemStack);

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        manager.refreshInventories(player);
    }
}
