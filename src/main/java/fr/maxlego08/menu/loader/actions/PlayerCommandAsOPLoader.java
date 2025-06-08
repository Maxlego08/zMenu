package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAsOPAction;

import java.io.File;
import java.util.List;

public class PlayerCommandAsOPLoader extends ActionLoader {

    public PlayerCommandAsOPLoader() {
        super("player_command_as_op","player_commands_as_op","player command as op",
                "player commands as op", "player-command-as-op","player-commands-as-op");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        List<String> commands = accessor.getStringList("commands");
        return new PlayerCommandAsOPAction(commands);
    }
}
