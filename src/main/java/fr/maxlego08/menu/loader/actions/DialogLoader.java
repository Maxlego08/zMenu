package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.api.DialogManager;
import fr.maxlego08.menu.requirement.actions.DialogAction;

import java.io.File;
import java.util.List;

public class DialogLoader extends ActionLoader {
    private final ZMenuPlugin plugin;
    private final DialogManager dialogManager;

    public DialogLoader(ZMenuPlugin plugin, DialogManager dialogManager) {
        super("dialog");
        this.plugin = plugin;
        this.dialogManager = dialogManager;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String dialog = accessor.getString("dialog");
        String plugin = accessor.getString("plugin", null);
        List<String> arguments = accessor.getStringList("arguments");
        return new DialogAction(this.dialogManager, this.plugin.getCommandManager(), dialog, plugin, arguments);
    }
}
