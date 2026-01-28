package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MessageToAction extends MessageAction {
    private final String targetPlayer;

    public MessageToAction(List<String> messages, boolean miniMessage, String targetPlayer) {
        super(messages, miniMessage);
        this.targetPlayer = targetPlayer;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        String parsedPlayer = papi(placeholders.parse(this.targetPlayer), player);
        Player target = Bukkit.getPlayerExact(parsedPlayer);
        super.execute(target == null ? player : target, button, inventory, placeholders);
    }


}
