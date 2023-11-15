package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.requirement.actions.BroadcastAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BroadcastLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "broadcast";
    }

    @Override
    public Action load(String path, Map<String, Object> map, File file) {
        boolean miniMessage = (boolean) map.getOrDefault("minimessage", true);
        List<String> messages = (List<String>) map.getOrDefault("messages", new ArrayList<>());
        return new BroadcastAction(messages, miniMessage);
    }
}
