package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.BroadcastMessageAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

@AutoActionLoader
public class BroadcastMessageLoader extends ActionLoader {

    public BroadcastMessageLoader() {
        super("broadcast_message", "broadcast message");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {

        boolean miniMessage = accessor.getBoolean("minimessage", accessor.getBoolean("mini-message", true));
        List<String> messages = accessor.getStringList("messages");

        return new BroadcastMessageAction(messages, miniMessage);
    }
}
