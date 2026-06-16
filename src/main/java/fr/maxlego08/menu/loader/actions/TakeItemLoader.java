package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.TakeItemAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

@AutoActionLoader
public class TakeItemLoader extends ActionLoader {
    private final MenuPlugin menuPlugin;

    public TakeItemLoader(MenuPlugin menuPlugin) {
        super("take_item", "take-item");
        this.menuPlugin = menuPlugin;
    }

    @Override
    public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        MenuItemStack menuItemStack;
        if (accessor.contains("item")) {
            menuItemStack = this.menuPlugin.getInventoryManager().loadItemStack(file, path, (Map<String, Object>) accessor.getObject("item"));
        } else {
            menuItemStack = this.menuPlugin.getInventoryManager().loadItemStack(file, path, accessor.map());
        }

        boolean useCache = accessor.getBoolean("use-cache", false);
        int amount = accessor.getInt("amount", 1);
        ItemVerification itemVerification = ItemVerification.valueOf(accessor.getString("verification", ItemVerification.SIMILAR.name()));
        return new TakeItemAction(menuItemStack, useCache, amount, itemVerification);
    }
}
