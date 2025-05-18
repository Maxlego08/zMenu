package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BackAction;

import java.io.File;

public class BackLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public BackLoader(MenuPlugin plugin) {
        super("back");
        this.plugin = plugin;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        return new BackAction(this.plugin.getInventoryManager());
    }
}
