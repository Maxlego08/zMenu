package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastAction;

import java.io.File;
import java.util.List;

public class BroadcastLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "broadcast";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        boolean miniMessage = accessor.getBoolean("minimessage", true);
        List<String> messages = accessor.getStringList("messages");
        return new BroadcastAction(messages, miniMessage);
    }
}
