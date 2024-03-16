package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.ZWebsiteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ButtonFolderNext extends ZButton {

    private final MenuPlugin plugin;

    public ButtonFolderNext(Plugin plugin) {
        this.plugin = (MenuPlugin) plugin;
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
        List<Folder> folders = manager.getFolders(folder);

        int folderPage = manager.getFolderPage();
        int maxPage = getMaxPage(folders, 5);

        if (folderPage < maxPage) {
            manager.openInventoriesInventory(player, manager.getInventoryPage(), folderPage + 1, folder.getId());
        }
    }
}
