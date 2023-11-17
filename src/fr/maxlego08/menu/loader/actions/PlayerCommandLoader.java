package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerCommandLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "player_command,player_commands";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        boolean inChat = accessor.getBoolean("commandInChat", false);
        List<String> commands = accessor.getStringList("commands");
        return new PlayerCommandAction(commands, inChat);
    }
}
