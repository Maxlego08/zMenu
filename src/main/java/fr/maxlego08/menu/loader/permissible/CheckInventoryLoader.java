package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoPermissibleLoader;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.requirement.permissible.ZCheckInventoryPermissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

@AutoPermissibleLoader
public class CheckInventoryLoader extends PermissibleLoader {
    private final InventoryManager inventoryManager;
    private final ButtonManager buttonManager;

    public CheckInventoryLoader(MenuPlugin plugin) {
        super("check-inventory");
        this.inventoryManager = plugin.getInventoryManager();
        this.buttonManager = plugin.getButtonManager();
    }

    @Override
    public @Nullable Permissible load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        int slot = accessor.getInt("slot", -1);
        if (slot == -1) return null;
        MenuItemStack menuItemStack;
        if (accessor.getObject("item") instanceof Map) {
            menuItemStack = this.inventoryManager.loadItemStack(file, (Map<String, Object>) accessor.getObject("item"));
        } else {
            menuItemStack = null;
        }
        boolean requirePlayerItem = accessor.getBoolean("require-player-item", false);
        boolean isInPlayerInventory = accessor.getBoolean("is-player-inventory", false);

        List<Action> denyActions = this.loadAction(this.buttonManager, accessor, "deny", path, file);
        List<Action> successActions = this.loadAction(this.buttonManager, accessor, "success", path, file);
        ItemStackSimilar itemStackSimilar = this.inventoryManager.getItemStackVerification(accessor.getString("type", "full")).orElseGet(FullSimilar::new);
        boolean inSpigotInventory = accessor.getBoolean("in-spigot-inventory", false);

        return new ZCheckInventoryPermissible(slot, menuItemStack, requirePlayerItem, isInPlayerInventory, denyActions, successActions, itemStackSimilar, inSpigotInventory);
    }
}
