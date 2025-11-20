package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ItemGiveAction;

import java.io.File;
import java.util.Map;

public class ItemGiveLoader extends ActionLoader {
    private final MenuPlugin menuPlugin;

    public ItemGiveLoader(MenuPlugin menuPlugin) {
        super("item_give", "item-give");
        this.menuPlugin = menuPlugin;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        MenuItemStack menuItemStack = menuPlugin.getInventoryManager().loadItemStack(file, path, (Map<String, Object>) accessor.getObject("item"));
        return new ItemGiveAction(menuItemStack);
    }
}
