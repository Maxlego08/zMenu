package fr.maxlego08.menu.hooks.dialogs.loader.actions;

import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import fr.maxlego08.menu.hooks.dialogs.requirement.actions.BroadcastAction;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogActionIntLoader;

import java.io.File;
import java.util.List;

public class BroadcastLoader implements DialogActionIntLoader {

    @Override
    public List<String> getActionNames() {
        return List.of("broadcast");
    }

    @Override
    public DialogAction load(String path, TypedMapAccessor accessor, File file) {
        List<String> messages = accessor.getStringList("messages");
        return new BroadcastAction(messages);

    }
}
