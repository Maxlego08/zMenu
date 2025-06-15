package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAction;

import java.io.File;
import java.util.List;

public class PlayerCommandLoader extends ActionLoader {

    public PlayerCommandLoader() {
        super("player_command", "player_commands", "player command", "player commands", "player-command,player-commands");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        boolean inChat = accessor.getBoolean("commandInChat", accessor.getBoolean("command-in-chat", false));
        List<String> commands = accessor.getStringList("commands");
        return new PlayerCommandAction(commands, inChat);
    }
}
