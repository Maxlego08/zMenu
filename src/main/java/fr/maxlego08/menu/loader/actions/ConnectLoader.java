package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ConnectAction;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class ConnectLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public ConnectLoader(MenuPlugin plugin) {
        super("connect");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String server = accessor.getString("server", "hub");
        return new ConnectAction(server, plugin);
    }
}
