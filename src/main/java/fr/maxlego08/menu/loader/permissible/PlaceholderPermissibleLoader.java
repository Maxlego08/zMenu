package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.permissible.ZPlaceholderPermissible;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

public class PlaceholderPermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public PlaceholderPermissibleLoader(ButtonManager buttonManager) {
        super("placeholder");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        PlaceholderAction action = PlaceholderAction.from(accessor.getString("action").toUpperCase());
        String placeholder = accessor.getString("placeholder", accessor.getString("placeHolder"));
        String value = accessor.getString("value");
        String targetPlayer = accessor.getString("target", null);
        boolean mathExpression = accessor.getBoolean("math", false);

        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);

        return new ZPlaceholderPermissible(action, placeholder, value, targetPlayer, denyActions, successActions, mathExpression);
    }
}
