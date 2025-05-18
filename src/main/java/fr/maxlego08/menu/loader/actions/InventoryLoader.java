package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.InventoryAction;

import java.io.File;
import java.util.List;

public class InventoryLoader extends ActionLoader {

    private final ZMenuPlugin plugin;

    public InventoryLoader(ZMenuPlugin plugin) {
        super("inventory", "inv");
        this.plugin = plugin;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String inventory = accessor.getString("inventory");
        String plugin = accessor.getString("plugin", null);
        String page = accessor.getString("page", "1");
        List<String> arguments = accessor.getStringList("arguments");
        return new InventoryAction(this.plugin.getInventoryManager(), this.plugin.getCommandManager(), inventory, plugin, page, arguments);
    }
}
