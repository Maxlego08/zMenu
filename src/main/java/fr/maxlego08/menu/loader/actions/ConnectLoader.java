package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ConnectAction;

import java.io.File;

public class ConnectLoader implements ActionLoader {

    private final ZMenuPlugin plugin;

    public ConnectLoader(ZMenuPlugin plugin) {
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
