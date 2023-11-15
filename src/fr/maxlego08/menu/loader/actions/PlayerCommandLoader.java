package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerCommandLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "player_command";
    }

    @Override
    public Action load(String path, Map<String, Object> map, File file) {
        boolean inChat = (boolean) map.getOrDefault("commandInChat", false);
        List<String> commands = (List<String>) map.getOrDefault("commands", new ArrayList<>());
        return new PlayerCommandAction(commands, inChat);
    }
}
