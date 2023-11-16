package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.MessageAction;
import fr.maxlego08.menu.requirement.actions.PlayerChatAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "chat";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        List<String> commands = accessor.getStringList("messages");
        return new PlayerChatAction(commands);
    }
}
