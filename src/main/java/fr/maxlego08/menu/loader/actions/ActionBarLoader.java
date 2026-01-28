package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ActionBarAction;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class ActionBarLoader extends ActionLoader {

    public ActionBarLoader() {
        super("action", "actionbar");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        boolean miniMessage = accessor.getBoolean("minimessage", true);
        String message = accessor.getString("message");
        return new ActionBarAction(message, miniMessage);
    }
}
