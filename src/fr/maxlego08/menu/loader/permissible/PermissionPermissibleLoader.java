package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZPermissionPermissible;

import java.io.File;

public class PermissionPermissibleLoader implements PermissibleLoader {

    @Override
    public String getKey() {
        return "permission";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        return new ZPermissionPermissible(accessor.getString("permission"));
    }
}
