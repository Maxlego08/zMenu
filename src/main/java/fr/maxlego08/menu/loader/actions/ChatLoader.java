package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.PlayerChatAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

public class ChatLoader extends ActionLoader {

    public ChatLoader() {
        super("chat");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        List<String> commands = accessor.getStringList("messages");
        return new PlayerChatAction(commands);
    }
}
