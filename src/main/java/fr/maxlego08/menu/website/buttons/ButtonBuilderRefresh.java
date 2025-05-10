package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.buttons.ZButton;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.utils.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ButtonBuilderRefresh extends ZButton {

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
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {

        if (!this.canUse) return;
        this.canUse = false;

        ItemBuilder itemBuilder = new ItemBuilder(Material.BARRIER, "§cPlease wait");
        inventory.getSpigotInventory().setItem(slot, itemBuilder.build());

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        manager.refreshInventories(player);
    }
}
