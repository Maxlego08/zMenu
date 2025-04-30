package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastAction extends Action {

    private final List<String> messages;
    private final boolean miniMessage;
    private final List<Permissible> requirements;

    public BroadcastAction(List<String> messages, boolean miniMessage, List<Permissible> requirements) {
        this.messages = messages;
        this.miniMessage = miniMessage;
        this.requirements = requirements;
    }

    @Override
    public void execute(Player sender, Button button, InventoryDefault inventory, Placeholders placeholders) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (requirements.isEmpty() || requirements.stream().allMatch(e -> e.hasPermission(player, button, inventory, placeholders))) {
                papi(placeholders.parse(this.messages), player, true).forEach(message -> {
                    message = message.replace("%sender%", sender.getName());
                    message = message.replace("%receiver%", player.getName());
                    if (miniMessage) {
                        Meta.meta.sendMessage(player, message);
                    } else {
                        player.sendMessage(message);
                    }
                });
            }
        });
    }
}
