package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ConnectAction;

import java.io.File;
import java.util.Map;

public class ConnectLoader implements ActionLoader {

    private final MenuPlugin plugin;

    public ConnectLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "connect";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String server = accessor.getString("server", "hub");
        return new ConnectAction(server, plugin);
    }
}
