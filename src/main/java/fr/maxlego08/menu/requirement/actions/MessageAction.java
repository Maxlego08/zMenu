package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import fr.robie.minimessageflow.TemplateList;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class MessageAction extends ActionHelper {

    private final List<String> messages;
    private final boolean miniMessage;
    private final transient TemplateList templates;

    public MessageAction(List<String> messages, boolean miniMessage) {
        this.messages = messages;
        this.miniMessage = miniMessage;
        this.templates = miniMessage ? compile(messages) : null;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        if (this.miniMessage) {
            for (Component message : this.templates.render(placeholders.values(), player)) {
                player.sendMessage(message);
            }
            return;
        }
        this.parseAndFlattenCommands(this.papi(placeholders.parse(this.messages), player), player).forEach(player::sendMessage);

    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "message");
        map.put("messages", this.messages);
        if (!this.miniMessage) map.put("minimessage", false);
    }
}
