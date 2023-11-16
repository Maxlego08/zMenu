package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.requirement.actions.ConsoleCommandAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsoleCommandLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "console_command";
    }

    @Override
    public Action load(String path, Map<String, Object> map, File file) {
        List<String> commands = (List<String>) map.getOrDefault("commands", new ArrayList<>());
        return new ConsoleCommandAction(commands);
    }
}
