package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.buttons.ZButton;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.ZWebsiteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ButtonFolderPrevious extends ZButton {

    private final ZMenuPlugin plugin;

    public ButtonFolderPrevious(Plugin plugin) {
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
        List<Folder> folders = manager.getFolders(folder);

        int folderPage = manager.getFolderPage();

        if (folderPage > 1) {
            manager.openInventoriesInventory(player, manager.getInventoryPage(), folderPage - 1, folder.getId());
        }
    }
}
