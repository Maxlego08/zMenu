package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.buttons.ZButton;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.api.engine.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ButtonFolders extends ZButton {

    private final ZMenuPlugin plugin;

    public ButtonFolders(Plugin plugin) {
        this.plugin = (ZMenuPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        int folderPage = manager.getFolderPage();
        Optional<Folder> optional = manager.getCurrentFolder();
        if (!optional.isPresent()) return;

        Folder folder = optional.get();
        List<Folder> folders = manager.getFolders(folder);
        Pagination<Folder> pagination = new Pagination<>();
        folders = pagination.paginate(folders, this.slots.size(), folderPage);

        for (int i = 0; i != Math.min(this.slots.size(), folders.size()); i++) {

            int slot = this.slots.get(i);
            Folder currentFolder = folders.get(i);
            displayFolder(slot, currentFolder, player, inventory, manager);
        }
    }

    private void displayFolder(int slot, Folder folder, Player player, InventoryEngine inventoryDefault, ZWebsiteManager manager) {
        MenuItemStack menuItemStack = this.getItemStack();

        Placeholders placeholders = new Placeholders();

        placeholders.register("quantity", String.valueOf(folder.getInventories().size()));
        placeholders.register("name", folder.getName());
        placeholders.register("id", String.valueOf(folder.getId()));

        ItemStack itemStack = menuItemStack.build(player, false, placeholders);
        inventoryDefault.addItem(slot, itemStack).setClick(event -> {
            manager.openInventoriesInventory(player, 1, 1, folder.getId());
        });
    }
}
