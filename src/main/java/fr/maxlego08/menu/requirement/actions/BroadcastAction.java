package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BroadcastAction extends ActionHelper {

    private final List<String> messages;
    private final boolean miniMessage;
    private final List<Permissible> requirements;

    public BroadcastAction(List<String> messages, boolean miniMessage, List<Permissible> requirements) {
        this.messages = messages;
        this.miniMessage = miniMessage;
        this.requirements = requirements;
    }

    @Override
    public void execute(@NonNull Player sender, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            boolean allowed = true;
            if (!requirements.isEmpty()) {
                for (Permissible requirement : requirements) {
                    if (!requirement.hasPermission(player, button, inventory, placeholders)) {
                        allowed = false;
                        break;
                    }
                }
            }
            if (allowed) {
                papi(placeholders.parse(this.messages), player).forEach(message -> {
                    message = message.replace("%sender%", sender.getName());
                    message = message.replace("%receiver%", player.getName());
                    if (miniMessage) {
                        inventory.getPlugin().getMetaUpdater().sendMessage(player, message);
                    } else {
                        player.sendMessage(message);
                    }
                });
            }
        });
    }
}
