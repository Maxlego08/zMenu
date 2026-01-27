package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class ActionBarAction extends ActionHelper {

    private final String message;
    private final boolean miniMessage;

    public ActionBarAction(String message, boolean miniMessage) {
        this.message = message;
        this.miniMessage = miniMessage;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        String finalMessage = papi(placeholders.parse(this.message), player);
        if (miniMessage) {
            inventory.getPlugin().getMetaUpdater().sendAction(player, finalMessage);
        } else {
            ActionBar.sendActionBar(player, finalMessage);
        }
    }
}
