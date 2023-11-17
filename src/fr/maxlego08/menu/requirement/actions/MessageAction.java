package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends Action {

    private final List<String> messages;
    private final boolean miniMessage;

    public MessageAction(List<String> messages, boolean miniMessage) {
        this.messages = messages;
        this.miniMessage = miniMessage;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory) {
        papi(this.messages, player).forEach(message -> {
            if (miniMessage) {
                Meta.meta.sendMessage(player, message);
            } else {
                player.sendMessage(message);
            }
        });
    }
}
