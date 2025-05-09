package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.ZPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZItemPermissible;

import java.io.File;
import java.util.List;

public class ItemPermissibleLoader extends ZPermissibleLoader {

    private final ZMenuPlugin plugin;

    public ItemPermissibleLoader(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "item";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        ButtonManager buttonManager = this.plugin.getButtonManager();

        ZMenuItemStack menuItemStack = new ZMenuItemStack(this.plugin.getInventoryManager(), file.getPath(), path);
        menuItemStack.setTypeMapAccessor(accessor);

        int amount = accessor.getInt("amount");
        ItemVerification itemVerification = ItemVerification.valueOf(accessor.getString("verification", ItemVerification.SIMILAR.name()));

        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);

        return new ZItemPermissible(menuItemStack, amount, denyActions, successActions, itemVerification);
    }
}
