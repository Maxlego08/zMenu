package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class PlayerCommandAction extends ActionHelper {

    private final List<TextTemplate> commands;
    private final boolean inChat;

    public PlayerCommandAction(List<String> commands, boolean inChat) {
        this.commands = TextService.texts(commands);
        this.inChat = inChat;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        var scheduler = inventory.getPlugin().getScheduler();
        scheduler.runAtEntity(player, w-> {
           render(this.commands, placeholders, player).forEach(command -> {
               if (this.inChat) {
                   player.chat("/" + command);
               } else {
                   Bukkit.dispatchCommand(player, command);
               }
           });
        });
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "player-command");
        map.put("commands", TextService.sources(this.commands));
        if (this.inChat) map.put("command-in-chat", true);
    }
}
