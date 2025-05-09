package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CloseAction;

import java.io.File;

public class CloseLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "close";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        return new CloseAction();
    }
}
