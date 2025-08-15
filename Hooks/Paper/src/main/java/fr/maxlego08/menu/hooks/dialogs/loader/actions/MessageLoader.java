package fr.maxlego08.menu.hooks.dialogs.loader.actions;

import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import fr.maxlego08.menu.hooks.dialogs.requirement.actions.MessageAction;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogActionIntLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessageLoader implements DialogActionIntLoader {
    @Override
    public List<String> getActionNames() {
        return List.of("messages", "message");
    }

    @Override
    public DialogAction load(String path, TypedMapAccessor accessor, File file) {
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
        return new MessageAction(messages);
    }


}
