package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CommandAndMessageAction;
import fr.maxlego08.menu.requirement.actions.ConsoleCommandAndMessageAction;

@AutoActionLoader
public class RandomConsoleCommandAndMessageLoader extends RandomCommandAndMessageLoader {

    public RandomConsoleCommandAndMessageLoader() {
        super("random-console-command-and-message", "random_console_command_and_message", "random_console_commands_and_messages", "random-console-command-and-message", "random-console-commands-and-messages");
    }

    @Override
    protected CommandAndMessageAction loadEntry(TypedMapAccessor entry, TypedMapAccessor parent) {
        return new ConsoleCommandAndMessageAction(
                entry.getStringList("commands"),
                MessageLoader.extractMessages(entry),
                getBoolean(entry, parent, false, "stop-on-failure", "stop_on_failure"),
                getBoolean(entry, parent, true, "minimessage", "mini-message"));
    }
}
