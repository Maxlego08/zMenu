package fr.maxlego08.menu.website.buttons;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.website.Folder;
import fr.maxlego08.menu.website.Inventory;
import fr.maxlego08.menu.website.ZWebsiteManager;
import fr.maxlego08.menu.zcore.utils.inventory.Pagination;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class ButtonInventories extends ZButton {

    private final MenuPlugin plugin;

    public ButtonInventories(Plugin plugin) {
        this.plugin = (MenuPlugin) plugin;
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
    public void onRender(Player player, InventoryDefault inventory) {

        ZWebsiteManager manager = this.plugin.getWebsiteManager();
        int inventoryPage = manager.getInventoryPage();
        Optional<Folder> optional = manager.getCurrentFolder();
        if (!optional.isPresent()) return;

        Folder folder = optional.get();
        List<Inventory> inventories = folder.getInventories();
        Pagination<Inventory> pagination = new Pagination<>();
        inventories = pagination.paginate(inventories, this.slots.size(), inventoryPage);

        for (int i = 0; i != Math.min(this.slots.size(), inventories.size()); i++) {

            int slot = this.slots.get(i);
            Inventory currentInventory = inventories.get(i);
            displayFolder(slot, currentInventory, player, inventory, manager);
        }
    }

    private void displayFolder(int slot, Inventory inventory, Player player, InventoryDefault inventoryDefault, ZWebsiteManager manager) {
        MenuItemStack menuItemStack = this.getItemStack();

        Placeholders placeholders = new Placeholders();

        placeholders.register("fileName", inventory.getFileName());
        placeholders.register("name", inventory.getName());
        placeholders.register("id", String.valueOf(inventory.getId()));
        placeholders.register("size", String.valueOf(inventory.getSize()));
        placeholders.register("createdAt", inventory.toCreateDate());
        placeholders.register("updatedAt", inventory.toUpdateDate());

        ItemStack itemStack = menuItemStack.build(player, false, placeholders);
        inventoryDefault.addItem(slot, itemStack).setClick(event -> manager.downloadInventory(player, inventory, event.isRightClick()));
    }
}
