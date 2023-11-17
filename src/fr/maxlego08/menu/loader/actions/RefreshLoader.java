package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CloseAction;
import fr.maxlego08.menu.requirement.actions.RefreshAction;

import java.io.File;

public class RefreshLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "refresh";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        return new RefreshAction();
    }
}
