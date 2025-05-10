package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.buttons.ZButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.Inventory;
import fr.maxlego08.menu.website.ZWebsiteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ButtonInventoryNext extends ZButton {

    private final ZMenuPlugin plugin;

    public ButtonInventoryNext(Plugin plugin) {
        this.plugin = (ZMenuPlugin) plugin;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        Optional<Folder> optional = manager.getCurrentFolder();
        if (!optional.isPresent()) return;

        Folder folder = optional.get();
        List<Inventory> inventories = folder.getInventories();

        int inventoryPage = manager.getInventoryPage();
        int maxPage = (inventories.size() / 34) + 1;

        if (inventoryPage < maxPage) {
            manager.openInventoriesInventory(player, inventoryPage + 1, manager.getFolderPage(), folder.getId());
        }
    }
}
