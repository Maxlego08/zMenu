package fr.maxlego08.menu.requirement.actions;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class ConsoleCommandAction extends ActionHelper {

    private final List<String> commands;

    public ConsoleCommandAction(List<String> commands) {
        this.commands = commands;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        var scheduler = inventory.getPlugin().getScheduler();
        List<String> parsedCommands = papi(placeholders.parse(parseAndFlattenCommands(commands, player)), player);

        Consumer<WrappedTask> runnable = w -> parsedCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));

        if (inventory.getPlugin().isFolia()) {
            scheduler.runNextTick(runnable);
        } else {
            runnable.accept(null);
        }
    }
}
