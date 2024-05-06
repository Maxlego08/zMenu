package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import org.bukkit.entity.Player;

public class ActionBarAction extends Action {

    private final String message;
    private final boolean miniMessage;

    public ActionBarAction(String message, boolean miniMessage) {
        this.message = message;
        this.miniMessage = miniMessage;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        String finalMessage = papi(placeholders.parse(this.message), player);
        if (miniMessage) {
            Meta.meta.sendAction(player, finalMessage);
        } else {
            ActionBar.sendActionBar(player, finalMessage);
        }
    }
}
