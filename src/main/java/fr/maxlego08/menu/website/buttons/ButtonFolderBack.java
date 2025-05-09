package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.ZWebsiteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class ButtonFolderBack extends ZButton {

    private final ZMenuPlugin plugin;

    public ButtonFolderBack(Plugin plugin) {
        this.plugin = (ZMenuPlugin) plugin;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        Optional<Folder> optional = manager.getCurrentFolder();
        if (!optional.isPresent()) return;

        Folder folder = optional.get();

        if (folder.getParentId() != -1) {
            manager.openInventoriesInventory(player, manager.getInventoryPage(), 1, folder.getParentId());
        }
    }
}
