package fr.maxlego08.menu.requirement.actions;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class ConsoleCommandAction extends ActionHelper {

    private final List<String> commands;
    private final boolean stopOnFailure;

    public ConsoleCommandAction(List<String> commands) {
        this(commands, false);
    }

    public ConsoleCommandAction(List<String> commands, boolean stopOnFailure) {
        this.commands = commands;
        this.stopOnFailure = stopOnFailure;
    }

    /**
     * @return true when a command that reports failure stops the remaining commands of this action.
     */
    public boolean isStopOnFailure() {
        return this.stopOnFailure;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        var scheduler = inventory.getPlugin().getScheduler();
        List<String> parsedCommands = this.parseAndFlattenCommands(this.papi(placeholders.parse(this.commands), player), player);

        Consumer<WrappedTask> runnable = w -> this.dispatch(parsedCommands);

        if (inventory.getPlugin().isFolia()) {
            scheduler.runNextTick(runnable);
        } else {
            runnable.accept(null);
        }
    }

    private void dispatch(List<String> parsedCommands) {
        for (String command : parsedCommands) {
            boolean success;
            try {
                success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } catch (Exception exception) {
                Logger.info("The console command \"" + command + "\" threw: " + exception.getMessage(), Logger.LogType.ERROR);
                success = false;
            }

            if (!success && this.stopOnFailure) {
                Logger.info("The console command \"" + command + "\" reported a failure, the following commands of this action were not run (stop-on-failure).", Logger.LogType.WARNING);
                return;
            }
        }
    }
}
