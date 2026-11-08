package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Makes the player run commands, then sends them messages.
 */
public class PlayerCommandAndMessageAction extends CommandAndMessageAction {

    private final List<String> commands;
    private final List<String> messages;
    private final boolean inChat;
    private final boolean miniMessage;
    private final PlayerCommandAction commandAction;
    private final MessageAction messageAction;

    public PlayerCommandAndMessageAction(List<String> commands, List<String> messages, boolean inChat, boolean miniMessage) {
        this.commands = commands;
        this.messages = messages;
        this.inChat = inChat;
        this.miniMessage = miniMessage;
        this.commandAction = new PlayerCommandAction(commands, inChat);
        this.messageAction = new MessageAction(messages, miniMessage);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.commandAction.execute(player, button, inventory, placeholders);
        this.messageAction.execute(player, button, inventory, placeholders);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "player-command-and-message");
        map.put("commands", this.commands);
        map.put("messages", this.messages);
        if (this.inChat) map.put("command-in-chat", true);
        if (!this.miniMessage) map.put("minimessage", false);
    }
}
