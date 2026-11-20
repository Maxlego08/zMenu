package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import fr.robie.minimessageflow.TemplateList;
import fr.robie.minimessageflow.Values;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BroadcastAction extends ActionHelper {

    private final List<String> messages;
    private final boolean miniMessage;
    private final List<Permissible> requirements;
    private final transient TemplateList templates;

    public BroadcastAction(List<String> messages, boolean miniMessage, List<Permissible> requirements) {
        this.messages = messages;
        this.miniMessage = miniMessage;
        this.requirements = requirements;
        this.templates = miniMessage ? compile(messages) : null;
    }

    @Override
    public void execute(@NonNull Player sender, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        Values senderValues = this.miniMessage ? placeholders.values().child().set("sender", sender.getName()) : null;
        Bukkit.getOnlinePlayers().forEach(player -> {
            boolean allowed = true;
            if (!this.requirements.isEmpty()) {
                for (Permissible requirement : this.requirements) {
                    if (!requirement.hasPermission(player, button, inventory, placeholders)) {
                        allowed = false;
                        break;
                    }
                }
            }
            if (!allowed) {
                return;
            }
            if (senderValues != null) {
                Values values = senderValues.child().set("receiver", player.getName());
                for (Component message : this.templates.render(values, player)) {
                    player.sendMessage(message);
                }
                return;
            }
            this.papi(placeholders.parse(this.messages), player).forEach(message ->
                    player.sendMessage(message.replace("%sender%", sender.getName()).replace("%receiver%", player.getName())));
        });
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "broadcast");
        map.put("messages", this.messages);
        if (!this.miniMessage) map.put("minimessage", false);
        if (!this.requirements.isEmpty()) {
            List<Map<String, Object>> serializedRequirements = new ArrayList<>(this.requirements.size());
            for (Permissible requirement : this.requirements) {
                serializedRequirements.add(requirement.serialize());
            }
            map.put("requirements", serializedRequirements);
        }
    }
}
