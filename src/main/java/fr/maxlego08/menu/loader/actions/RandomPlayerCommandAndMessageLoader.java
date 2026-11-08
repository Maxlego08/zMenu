package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CommandAndMessageAction;
import fr.maxlego08.menu.requirement.actions.PlayerCommandAndMessageAction;

@AutoActionLoader
public class RandomPlayerCommandAndMessageLoader extends RandomCommandAndMessageLoader {

    public RandomPlayerCommandAndMessageLoader() {
        super("random-player-command-and-message", "random_player_command_and_message", "random_player_commands_and_messages", "random-player-command-and-message", "random-player-commands-and-messages");
    }

    @Override
    protected CommandAndMessageAction loadEntry(TypedMapAccessor entry, TypedMapAccessor parent) {
        return new PlayerCommandAndMessageAction(
                entry.getStringList("commands"),
                MessageLoader.extractMessages(entry),
                getBoolean(entry, parent, false, "command-in-chat", "commandInChat"),
                getBoolean(entry, parent, true, "minimessage", "mini-message"));
    }
}
