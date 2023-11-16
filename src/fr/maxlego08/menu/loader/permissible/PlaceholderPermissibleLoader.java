package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZPlaceholderPermissible;

import java.io.File;

public class PlaceholderPermissibleLoader implements PermissibleLoader {

    @Override
    public String getKey() {
        return "placeholder";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        PlaceholderAction action = PlaceholderAction.from(accessor.getString("action").toUpperCase());
        String placeholder = accessor.getString("placeholder", accessor.getString("placeHolder"));
        String value = accessor.getString("value");
        return new ZPlaceholderPermissible(action, placeholder, value);
    }
}
