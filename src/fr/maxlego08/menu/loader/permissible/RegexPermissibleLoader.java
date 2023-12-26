package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.ZPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZRegexPermissible;

import java.io.File;
import java.util.List;

public class RegexPermissibleLoader extends ZPermissibleLoader {

    private final ButtonManager buttonManager;

    public RegexPermissibleLoader(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    @Override
    public String getKey() {
        return "regex";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        String placeholder = accessor.getString("input");
        String regex = accessor.getString("regex");

        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);

        return new ZRegexPermissible(regex, placeholder, denyActions, successActions);
    }
}
