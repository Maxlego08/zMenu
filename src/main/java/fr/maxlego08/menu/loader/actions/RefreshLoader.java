package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.RefreshAction;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class RefreshLoader extends ActionLoader {

    public RefreshLoader() {
        super("refresh");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        return new RefreshAction();
    }
}
