package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an action that can be executed based on certain conditions.
 */
public abstract class Action {

    /**
     * The delay in ticks before the action is executed.
     * A value of 0 means no delay.
     */
    private int delay;

    /**
     * Executes the action for the specified player.
     *
     * @param player          The player who triggers the action.
     * @param button          The button associated with the action.
     * @param inventoryEngine The inventory engine managing the inventory.
     * @param placeholders    Placeholders
     */
    protected abstract void execute(Player player, Button button, InventoryEngine inventoryEngine, Placeholders placeholders);

    public void preExecute(Player player, Button button, InventoryEngine inventoryEngine, Placeholders placeholders) {
        if (delay == 0) execute(player, button, inventoryEngine, placeholders);
        else {
            inventoryEngine.getPlugin().getScheduler().runAtEntityLater(player, () -> execute(player, button, inventoryEngine, placeholders), this.delay);
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Parses and flattens a list of commands by splitting each command by newline, replacing a
     * placeholder with the player's name, and returning a flattened list of processed commands.
     *
     * @param liste  The list of command strings to process and flatten.
     * @param player The player whose name will replace the "%player%" placeholder in the commands.
     * @return A list of commands that have been processed and flattened with placeholders replaced.
     */
    protected List<String> parseAndFlattenCommands(List<String> liste, Player player) {
        return liste.stream().flatMap(cmd -> Stream.of(cmd.split("\n"))).map(cmd -> cmd.replace("%player%", player.getName())).collect(Collectors.toList());
    }
}
