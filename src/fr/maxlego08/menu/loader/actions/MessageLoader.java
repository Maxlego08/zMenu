package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastAction;
import fr.maxlego08.menu.requirement.actions.MessageAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "message,messages";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        boolean miniMessage = accessor.getBoolean("minimessage", true);
        List<String> messages = accessor.getStringList("messages");
        return new MessageAction(messages, miniMessage);
    }
}
