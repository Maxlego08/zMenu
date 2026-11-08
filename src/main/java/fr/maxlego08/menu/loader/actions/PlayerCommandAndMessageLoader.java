package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAndMessageAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

@AutoActionLoader
public class PlayerCommandAndMessageLoader extends ActionLoader {

    public PlayerCommandAndMessageLoader() {
        super("player_command_and_message", "player_commands_and_messages", "player-command-and-message", "player-commands-and-messages");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        List<String> commands = accessor.getStringList("commands");
        List<String> messages = MessageLoader.extractMessages(accessor);
        boolean inChat = accessor.getBoolean("commandInChat", accessor.getBoolean("command-in-chat", false));
        boolean miniMessage = accessor.getBoolean("minimessage", accessor.getBoolean("mini-message", true));
        return new PlayerCommandAndMessageAction(commands, messages, inChat, miniMessage);
    }
}
