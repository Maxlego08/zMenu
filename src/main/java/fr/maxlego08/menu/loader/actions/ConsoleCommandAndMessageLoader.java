package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ConsoleCommandAndMessageAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

@AutoActionLoader
public class ConsoleCommandAndMessageLoader extends ActionLoader {

    public ConsoleCommandAndMessageLoader() {
        super("console_command_and_message", "console_commands_and_messages", "console-command-and-message", "console-commands-and-messages");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        List<String> commands = accessor.getStringList("commands");
        List<String> messages = MessageLoader.extractMessages(accessor);
        boolean stopOnFailure = accessor.getBoolean("stop-on-failure", accessor.getBoolean("stop_on_failure", false));
        boolean miniMessage = accessor.getBoolean("minimessage", accessor.getBoolean("mini-message", true));
        return new ConsoleCommandAndMessageAction(commands, messages, stopOnFailure, miniMessage);
    }
}
