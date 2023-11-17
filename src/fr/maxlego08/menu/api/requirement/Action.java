package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
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
     * @param player The player who triggers the action.
     */
    protected abstract void execute(Player player, Button button, InventoryDefault inventory);

    public void preExecute(Player player, Button button, InventoryDefault inventory) {
        if (delay == 0) execute(player, button, inventory);
        else inventory.getPlugin().getScheduler().runTaskLater(player.getLocation(), this.delay, () -> {
            execute(player, button, inventory);
        });
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
