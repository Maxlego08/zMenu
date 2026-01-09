package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BackAction;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class BackLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public BackLoader(MenuPlugin plugin) {
        super("back");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        return new BackAction(this.plugin.getInventoryManager());
    }
}
