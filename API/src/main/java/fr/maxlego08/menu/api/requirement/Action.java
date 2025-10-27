package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an action that can be executed based on certain conditions.
 */
public abstract class Action {

    /**
     * The delay in ticks before the action is executed.
     * A value of 0 means no delay.
     */
    private int delay;
    private float chance;

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
        placeholders.register("player", player.getName());
        if (chance < 100 && Math.random() > (chance / 100.0f)) return;
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

    public float getChance() {return chance;}

    public void setChance(float chance) {
        if ( chance < 0 || chance > 100) {
            if (Config.enableDebug){
                throw new IllegalArgumentException("Chance must be between 0 and 100");
            }
            chance = 100;
        };
        this.chance = chance;
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
        List<String> commands = new ArrayList<>();
        final String playerName = player.getName();
        for (String cmd : liste) {
            String[] split = cmd.split("\n");
            for (String part : split) {
                commands.add(part.replace("%player%", playerName));
            }
        }
        return commands;
    }
}
