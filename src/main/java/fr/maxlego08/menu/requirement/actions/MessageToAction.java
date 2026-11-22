package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class MessageToAction extends MessageAction {
    private final TextTemplate targetPlayer;

    public MessageToAction(List<String> messages, boolean miniMessage, String targetPlayer) {
        super(messages, miniMessage);
        this.targetPlayer = TextService.text(targetPlayer);
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        String parsedPlayer = TextService.render(this.targetPlayer, placeholders, player);
        Player target = Bukkit.getPlayerExact(parsedPlayer);
        super.execute(target == null ? player : target, button, inventory, placeholders);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        super.serializeProperties(map);
        map.put("type", "message-to");
        if (!TextService.source(this.targetPlayer).isEmpty()) map.put("target-player", TextService.source(this.targetPlayer));
    }


}
