package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoPermissibleLoader;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZItemPermissible;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;
import java.util.Map;

@AutoPermissibleLoader
public class ItemPermissibleLoader extends PermissibleLoader {

    private final MenuPlugin plugin;

    public ItemPermissibleLoader(MenuPlugin plugin) {
        super("item");
        this.plugin = plugin;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        ButtonManager buttonManager = this.plugin.getButtonManager();

        MenuItemStack menuItemStack;
        if (accessor.contains("item")) {
            menuItemStack = this.plugin.getInventoryManager().loadItemStack(file, path, (Map<String, Object>) accessor.getObject("item"));
        } else {
            menuItemStack = this.plugin.getInventoryManager().loadItemStack(file, path, accessor.map());
        }

        int amount = accessor.getInt("amount");
        ItemVerification itemVerification = ItemVerification.valueOf(accessor.getString("verification", ItemVerification.SIMILAR.name()));

        List<Action> denyActions = this.loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = this.loadAction(buttonManager, accessor, "success", path, file);

        return new ZItemPermissible(menuItemStack, amount, denyActions, successActions, itemVerification);
    }
}
