package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.InventoryAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

@AutoActionLoader
public class InventoryLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public InventoryLoader(MenuPlugin plugin) {
        super("inventory", "inv");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String inventory = accessor.getString("inventory");
        String plugin = accessor.getString("plugin", null);
        String page = accessor.getString("page", "1");
        List<String> arguments = accessor.getStringList("arguments");
        return new InventoryAction(this.plugin.getInventoryManager(), this.plugin.getCommandManager(), inventory, plugin, page, arguments);
    }
}
