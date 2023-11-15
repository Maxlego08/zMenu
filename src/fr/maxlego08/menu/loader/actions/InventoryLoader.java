package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
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
    public Action load(String path, Map<String, Object> map, File file) {
        String inventory = (String) map.getOrDefault("inventory", null);
        String plugin = (String) map.getOrDefault("plugin", null);
        int page = Integer.parseInt((String) map.getOrDefault("page", "1"));
        return new InventoryAction(this.plugin.getInventoryManager(), inventory, plugin, page);
    }
}
