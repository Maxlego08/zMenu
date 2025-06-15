package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.luckperms.ZLuckPermPermissible;

import java.io.File;
import java.util.List;

public class LuckPermPermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public LuckPermPermissibleLoader(ButtonManager buttonManager) {
        super("luckperm");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);
        return new ZLuckPermPermissible(denyActions, successActions, accessor.getString("group"));
    }
}
