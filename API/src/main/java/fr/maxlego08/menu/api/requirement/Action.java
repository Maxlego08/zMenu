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
    protected boolean debug = false;

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

    /**
     * Runs the action, ignoring whether the ones after it should still run.
     *
     * <p>Kept so that existing callers and existing subclasses keep working unchanged. Anything
     * iterating a list of actions should prefer {@link #preExecuteChain} so that an action which
     * failed a precondition can stop the rest of the list.</p>
     */
    public void preExecute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        this.preExecuteChain(player, button, inventoryEngine, placeholders);
    }

    /**
     * Runs the action and reports whether the actions after it should still run.
     *
     * <p>An action with a delay always reports {@link ActionResult#CONTINUE}: it has not run yet
     * when this returns, so it has nothing to report. Do not put a delay on an action whose outcome
     * is meant to gate the ones after it.</p>
     *
     * @param player          The player who triggers the action.
     * @param button          The button associated with the action.
     * @param inventoryEngine The inventory engine managing the inventory.
     * @param placeholders    Placeholders
     * @return Whether the remaining actions of the list should run.
     */
    public ActionResult preExecuteChain(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        placeholders.register("player", player.getName());
        if (this.chance < 100 && Math.random() > (this.chance / 100.0f)) {
            for (Action denyChanceAction : this.denyChanceActions) {
                if (denyChanceAction.preExecuteChain(player, button, inventoryEngine, placeholders) == ActionResult.STOP) {
                    return ActionResult.STOP;
                }
            }
            return ActionResult.CONTINUE;
        }
        if (this.delay == 0) return this.executeChain(player, button, inventoryEngine, placeholders);

        inventoryEngine.getPlugin().getScheduler().runAtEntityLater(player, () -> this.executeChain(player, button, inventoryEngine, placeholders), this.delay);
        return ActionResult.CONTINUE;
    }

    /**
     * Performs the action and reports whether the actions after it should still run.
     *
     * <p>The default simply runs {@link #execute} and lets the list carry on, so a subclass that
     * only implements {@code execute} behaves exactly as before. Override this instead when the
     * action can fail in a way that must stop whatever follows it.</p>
     *
     * @param player          The player who triggers the action.
     * @param button          The button associated with the action.
     * @param inventoryEngine The inventory engine managing the inventory.
     * @param placeholders    Placeholders
     * @return Whether the remaining actions of the list should run.
     */
    protected ActionResult executeChain(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        this.execute(player, button, inventoryEngine, placeholders);
        return ActionResult.CONTINUE;
    }

    @Contract(pure= true)
    public int getDelay() {
        return this.delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Contract(pure= true)
    public float getChance() {
        return this.chance;
    }

    public void setChance(float chance) {
        if (chance < 0 || chance > 100) {
            if (Configuration.enableDebug) {
                throw new IllegalArgumentException("Chance must be between 0 and 100");
            }
            chance = 100;
        }
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
        return this.denyChanceActions;
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
