package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;

/**
 * Represents an action that can be executed based on certain conditions.
 */
public abstract class Action extends ZUtils {

    private int delay;

    /**
     * Executes the action for the specified player.
     *
     * @param player       The player who triggers the action.
     * @param placeholders Placeholders
     */
    protected abstract void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders);

    public void preExecute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        if (delay == 0) execute(player, button, inventory, placeholders);
        else inventory.getPlugin().getScheduler().runTaskLater(player.getLocation(), this.delay, () -> {
            execute(player, button, inventory, placeholders);
        });
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
