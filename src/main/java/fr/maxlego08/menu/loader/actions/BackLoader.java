package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BackAction;

import java.io.File;

public class BackLoader implements ActionLoader {

    private final ZMenuPlugin plugin;

    public BackLoader(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "back";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        return new BackAction(this.plugin.getInventoryManager());
    }
}
