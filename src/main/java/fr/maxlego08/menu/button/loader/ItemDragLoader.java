package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZItemDragButton;
import fr.maxlego08.menu.itemstack.FullSimilar;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemDragLoader extends ButtonLoader {

    private final InventoryManager inventoryManager;
    private final DupeManager dupeManager;

    public ItemDragLoader(MenuPlugin plugin) {
        super(plugin, "item_drag");
        this.inventoryManager = plugin.getInventoryManager();
        this.dupeManager = plugin.getDupeManager();
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        ZItemDragButton button = new ZItemDragButton(dupeManager);
        String check_element = configuration.contains(path + "check_item") ? "check_item" : configuration.contains(path + "check-item") ? "check-item" : null;

        if (check_element != null) {
            MenuItemStack menuItemStack = inventoryManager.loadItemStack(configuration, path + check_element + ".item.", defaultButtonValue.getFile());
            String type = configuration.getString(path + check_element + ".type", "full");
            ItemStackSimilar itemStackSimilar = this.inventoryManager.getItemStackVerification(type).orElseGet(FullSimilar::new);
            button.setCheckItem(menuItemStack, itemStackSimilar);
        }

        String error_element = configuration.contains(path + "error_item") ? "error_item" : configuration.contains(path + "error-item") ? "error-item" : null;
        if (error_element != null) {
            int ticks = configuration.getInt(path + error_element + ".duration", 20);
            MenuItemStack menuItemStack = inventoryManager.loadItemStack(configuration, path + error_element + ".item.", defaultButtonValue.getFile());
            button.setErrorItem(menuItemStack, this.inventoryManager.getScheduler(), ticks);
        }

        return button;
    }
}