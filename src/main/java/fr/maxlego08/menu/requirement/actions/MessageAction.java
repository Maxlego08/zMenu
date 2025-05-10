package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends ActionHelper {

    private final List<String> messages;
    private final boolean miniMessage;

    public MessageAction(List<String> messages, boolean miniMessage) {
        this.messages = messages;
        this.miniMessage = miniMessage;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        papi(placeholders.parse(this.parseAndFlattenCommands(this.messages, player)), player).forEach(message -> {
            if (miniMessage) {
                inventory.getPlugin().getMetaUpdater().sendMessage(player, message);
            } else {
                player.sendMessage(message);
            }
        });
    }
}
