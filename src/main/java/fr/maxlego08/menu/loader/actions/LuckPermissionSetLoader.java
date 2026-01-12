package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.luckperms.LuckpermAction;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class LuckPermissionSetLoader extends ActionLoader {

    public LuckPermissionSetLoader() {
        super("permission-set", "permission set", "set permission", "set-permission");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String permission = accessor.getString("permission");
        boolean value = accessor.getBoolean("value", true);
        return new LuckpermAction(permission, value);
    }
}
