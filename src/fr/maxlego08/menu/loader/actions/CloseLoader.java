package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.requirement.actions.CloseAction;
import fr.maxlego08.menu.requirement.actions.MessageAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CloseLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "close";
    }

    @Override
    public Action load(String path, Map<String, Object> map, File file) {
        return new CloseAction();
    }
}
