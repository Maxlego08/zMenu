package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an action that can be executed based on certain conditions.
 */
@SuppressWarnings("unused")
public abstract class Action {

    private final List<Action> denyChanceActions = new ArrayList<>();
    /**
     * The delay in ticks before the action is executed.
     * A value of 0 means no delay.
     */
    private int delay;
    private float chance;

    /**
     * The type of the action.
     */
    private String type;

    /**
     * Executes the action for the specified player.
     *
     * @param player          The player who triggers the action.
     * @param button          The button associated with the action.
     * @param inventoryEngine The inventory engine managing the inventory.
     * @param placeholders    Placeholders
     */
    protected abstract void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders);

    public void preExecute(@NotNull Player player,@Nullable Button button,@NotNull InventoryEngine inventoryEngine,@NotNull Placeholders placeholders) {
        placeholders.register("player", player.getName());
        if (chance < 100 && Math.random() > (chance / 100.0f)) {
            for (Action denyChanceAction : denyChanceActions) {
                denyChanceAction.preExecute(player, button, inventoryEngine, placeholders);
            }
            return;
        }
        ;
        if (delay == 0) execute(player, button, inventoryEngine, placeholders);
        else {
            inventoryEngine.getPlugin().getScheduler().runAtEntityLater(player, () -> execute(player, button, inventoryEngine, placeholders), this.delay);
        }
    }

    @Contract(pure= true)
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Contract(pure= true)
    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        if (chance < 0 || chance > 100) {
            if (Configuration.enableDebug) {
                throw new IllegalArgumentException("Chance must be between 0 and 100");
            }
            chance = 100;
        }
        ;
        this.chance = chance;
    }

    /**
     * Returns the list of deny chance actions associated with this action.
     *
     * @return The list of deny chance actions.
     */
    @Contract(pure= true)
    @NotNull
    public List<Action> getDenyChanceActions() {
        return denyChanceActions;
    }

    /**
     * Sets the list of deny chance actions associated with this action.
     *
     * @param denyChanceActions The list of deny chance actions.
     */
    public void setDenyChanceActions(@Nullable List<Action> denyChanceActions) {
        this.denyChanceActions.clear();
        if (denyChanceActions != null) {
            this.denyChanceActions.addAll(denyChanceActions);
        }
    }

    /**
     * Parses and flattens a list of commands by splitting each command by newline, replacing a
     * placeholder with the player's name, and returning a flattened list of processed commands.
     *
     * @param liste  The list of command strings to process and flatten.
     * @param player The player whose name will replace the "%player%" placeholder in the commands.
     * @return A list of commands that have been processed and flattened with placeholders replaced.
     */
    @NotNull
    protected List<String> parseAndFlattenCommands(@NotNull List<String> liste,@NotNull Player player) {
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

    public void setType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return this.type;
    }
}
