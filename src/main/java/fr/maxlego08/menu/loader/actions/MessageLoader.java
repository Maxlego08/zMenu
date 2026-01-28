package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.MessageAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageLoader extends ActionLoader {

    public MessageLoader() {
        super("message", "messages");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        boolean miniMessage = accessor.getBoolean("minimessage", accessor.getBoolean("mini-message", true));
        List<String> messages = extractMessages(accessor);
        return new MessageAction(messages, miniMessage);
    }

    public static List<String> extractMessages(TypedMapAccessor accessor) {
        List<String> messages = new ArrayList<>();
        if (accessor.contains("message")) {
            messages.add(accessor.getString("message"));
        } else if (accessor.contains("messages")) {
            Object element = accessor.getObject("messages", new ArrayList<>());
            if (element instanceof String) {
                messages.add((String) element);
            } else {
                messages = accessor.getStringList("messages");
            }
        }
        return messages;
    }

}
