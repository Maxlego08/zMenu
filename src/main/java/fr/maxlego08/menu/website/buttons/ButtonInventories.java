package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.Inventory;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.api.engine.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ButtonInventories extends Button {

    private final ZMenuPlugin plugin;

    public ButtonInventories(Plugin plugin) {
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
        int inventoryPage = manager.getInventoryPage();
        Optional<Folder> optional = manager.getCurrentFolder();
        if (optional.isEmpty()) return;

        Folder folder = optional.get();
        List<Inventory> inventories = folder.inventories();
        Pagination<Inventory> pagination = new Pagination<>();
        inventories = pagination.paginate(inventories, this.slots.size(), inventoryPage);

        for (int i = 0; i != Math.min(this.slots.size(), inventories.size()); i++) {

            int slot = this.slots.get(i);
            Inventory currentInventory = inventories.get(i);
            displayFolder(slot, currentInventory, player, inventory, manager);
        }
    }

    private void displayFolder(int slot, Inventory inventory, Player player, InventoryEngine inventoryDefault, ZWebsiteManager manager) {
        MenuItemStack menuItemStack = this.getItemStack();

        Placeholders placeholders = new Placeholders();

        placeholders.register("fileName", inventory.fileName());
        placeholders.register("name", inventory.name() == null ? "Inventory" : inventory.name());
        placeholders.register("id", String.valueOf(inventory.id()));
        placeholders.register("size", String.valueOf(inventory.size()));
        placeholders.register("createdAt", inventory.toCreateDate());
        placeholders.register("updatedAt", inventory.toUpdateDate());

        ItemStack itemStack = menuItemStack.build(player, false, placeholders);
        inventoryDefault.addItem(slot, itemStack).setClick(event -> manager.downloadInventory(player, inventory, event.isRightClick()));
    }
}
