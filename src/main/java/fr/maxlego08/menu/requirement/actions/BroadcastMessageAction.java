package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import fr.robie.minimessageflow.TemplateList;
import fr.robie.minimessageflow.Values;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class BroadcastMessageAction extends ActionHelper {

    private final List<String> messages;
    private final boolean miniMessage;
    private final transient TemplateList templates;

    public BroadcastMessageAction(List<String> messages, boolean miniMessage) {
        this.messages = messages;
        this.miniMessage = miniMessage;
        this.templates = miniMessage ? compile(messages) : null;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        if (this.miniMessage) {
            Values senderValues = placeholders.values().child().set("sender", player.getName());
            Bukkit.getOnlinePlayers().forEach(target -> {
                Values values = senderValues.child().set("receiver", target.getName());
                for (Component message : this.templates.render(values, player)) {
                    target.sendMessage(message);
                }
            });
            return;
        }
        List<String> finalMessages = this.papi(placeholders.parse(this.messages), player);
        Bukkit.getOnlinePlayers().forEach(target -> finalMessages.forEach(message ->
                target.sendMessage(message.replace("%sender%", player.getName()).replace("%receiver%", target.getName()))));
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "broadcast message");
        map.put("messages", this.messages);
        if (!this.miniMessage) map.put("minimessage", false);
    }

}
