package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.utils.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ButtonBuilderRefresh extends ZButton {

    private final MenuPlugin plugin;
    private boolean canUse = true;

    public ButtonBuilderRefresh(Plugin plugin) {
        this.plugin = (MenuPlugin) plugin;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {

        if (!this.canUse) return;
        this.canUse = false;

        ItemBuilder itemBuilder = new ItemBuilder(Material.BARRIER, "§cPlease wait");
        inventory.getSpigotInventory().setItem(slot, itemBuilder.build());

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        manager.refreshInventories(player);
    }
}
