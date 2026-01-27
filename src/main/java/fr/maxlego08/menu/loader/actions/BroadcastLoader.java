package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BroadcastLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public BroadcastLoader(MenuPlugin plugin) {
        super("broadcast");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        boolean miniMessage = accessor.getBoolean("minimessage", accessor.getBoolean("mini-message", true));
        List<String> messages = accessor.getStringList("messages");
        List<Permissible> requirements = new ArrayList<>();
        if (accessor.contains("requirements")) {
            requirements = this.plugin.getButtonManager().loadPermissible((List<Map<String, Object>>) accessor.getObject("requirements"), path, file);
        }
        return new BroadcastAction(messages, miniMessage, requirements);
    }
}
