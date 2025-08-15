package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.dialogs.DialogManager;
import fr.maxlego08.menu.requirement.actions.DialogAction;

import java.io.File;

public class DialogLoader extends ActionLoader {
    private final MenuPlugin plugin;
    private final DialogManager dialogManager;

    public DialogLoader(MenuPlugin plugin, DialogManager dialogManager) {
        super("dialog");
        this.plugin = plugin;
        this.dialogManager = dialogManager;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String dialog = accessor.getString("dialog");
        String pluginName = accessor.getString("plugin", plugin.getName());
        return new DialogAction(dialogManager, dialog, pluginName);
    }
}
