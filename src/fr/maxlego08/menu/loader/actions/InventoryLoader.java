package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.InventoryAction;

import java.io.File;
import java.util.Map;

public class InventoryLoader implements ActionLoader {

    private final MenuPlugin plugin;

    public InventoryLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "inventory";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String inventory = accessor.getString("inventory");
        String plugin = accessor.getString("plugin");
        int page = accessor.getInt("page", 1);
        return new InventoryAction(this.plugin.getInventoryManager(), inventory, plugin, page);
    }
}
