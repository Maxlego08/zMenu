package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Runs console commands, then sends messages to the player.
 */
public class ConsoleCommandAndMessageAction extends CommandAndMessageAction {

    private final List<String> commands;
    private final List<String> messages;
    private final boolean stopOnFailure;
    private final boolean miniMessage;
    private final ConsoleCommandAction commandAction;
    private final MessageAction messageAction;

    public ConsoleCommandAndMessageAction(List<String> commands, List<String> messages, boolean stopOnFailure, boolean miniMessage) {
        this.commands = commands;
        this.messages = messages;
        this.stopOnFailure = stopOnFailure;
        this.miniMessage = miniMessage;
        this.commandAction = new ConsoleCommandAction(commands, stopOnFailure);
        this.messageAction = new MessageAction(messages, miniMessage);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.commandAction.execute(player, button, inventory, placeholders);
        this.messageAction.execute(player, button, inventory, placeholders);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "console-command-and-message");
        map.put("commands", this.commands);
        map.put("messages", this.messages);
        if (this.stopOnFailure) map.put("stop-on-failure", true);
        if (!this.miniMessage) map.put("minimessage", false);
    }
}
