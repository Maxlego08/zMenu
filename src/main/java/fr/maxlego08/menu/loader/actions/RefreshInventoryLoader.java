package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.RefreshAction;
import fr.maxlego08.menu.requirement.actions.RefreshInventoryAction;

import java.io.File;

public class RefreshInventoryLoader extends ActionLoader {

    public RefreshInventoryLoader() {
        super("refresh-inventory", "ri", "refresh inventory");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        return new RefreshInventoryAction();
    }
}
