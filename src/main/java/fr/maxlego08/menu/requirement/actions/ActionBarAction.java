package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import org.bukkit.entity.Player;
import fr.robie.minimessageflow.Template;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ActionBarAction extends ActionHelper {

    private final String message;
    private final boolean miniMessage;
    private final transient Template template;

    public ActionBarAction(String message, boolean miniMessage) {
        this.message = message;
        this.miniMessage = miniMessage;
        this.template = miniMessage ? compile(message) : null;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        if (this.template != null) {
            player.sendActionBar(this.template.render(placeholders.values(), player));
        } else if (!this.miniMessage) {
            ActionBar.sendActionBar(player, this.papi(placeholders.parse(this.message), player));
        }
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "actionbar");
        map.put("message", this.message);
        if (!this.miniMessage) map.put("minimessage", false);
    }
}
