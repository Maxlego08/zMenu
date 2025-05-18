package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.luckperms.LuckPermissionSet;

import java.io.File;

public class LuckPermissionSetLoader extends ActionLoader {

    public LuckPermissionSetLoader() {
        super("permission-set", "permission set", "set permission", "set-permission");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String permission = accessor.getString("permission");
        return new LuckPermissionSet(permission);
    }
}
