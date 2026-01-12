package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

public class PermissionPermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public PermissionPermissibleLoader(ButtonManager buttonManager) {
        super("permission");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);
        return new ZPermissionPermissible(accessor.getString("permission"), denyActions, successActions);
    }
}
