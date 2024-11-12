package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ConsoleCommandAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConsoleCommandLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "console_command,console_commands,console commands,console command,command,commands";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        List<String> commands = accessor.getStringList("commands");
        return new ConsoleCommandAction(commands);
    }
}
