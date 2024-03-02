package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.ZPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZPlaceholderPermissible;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceholderPermissibleLoader extends ZPermissibleLoader {

    private final ButtonManager buttonManager;

    public PlaceholderPermissibleLoader(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    @Override
    public String getKey() {
        return "placeholder";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        PlaceholderAction action = PlaceholderAction.from(accessor.getString("action").toUpperCase());
        String placeholder = accessor.getString("placeholder", accessor.getString("placeHolder"));
        String value = accessor.getString("value");
        String targetPlayer = accessor.getString("target");

        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);

        return new ZPlaceholderPermissible(action, placeholder, value, targetPlayer, denyActions, successActions);
    }
}
