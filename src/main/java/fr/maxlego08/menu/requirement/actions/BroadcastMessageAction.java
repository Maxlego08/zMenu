package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BroadcastMessageAction extends ActionHelper {

    private final List<String> messages;
    private final boolean miniMessage;

    public BroadcastMessageAction(List<String> messages, boolean miniMessage) {
        this.messages = messages;
        this.miniMessage = miniMessage;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        List<String> finalMessages = this.papi(placeholders.parse(this.messages), player);
        Bukkit.getOnlinePlayers().forEach(target -> finalMessages.forEach(message -> {
            String finalMessage = message.replace("%sender%", player.getName()).replace("%receiver%", target.getName());
            if (this.miniMessage) {
                inventory.getPlugin().getMetaUpdater().sendMessage(target, finalMessage);
            } else {
                target.sendMessage(finalMessage);
            }
        }));
    }

}
