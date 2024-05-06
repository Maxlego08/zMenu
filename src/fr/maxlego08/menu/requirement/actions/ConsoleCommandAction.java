package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ConsoleCommandAction extends Action {

    private final List<String> commands;

    public ConsoleCommandAction(List<String> commands) {
        this.commands = commands;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        ZScheduler scheduler = inventory.getPlugin().getScheduler();
        scheduler.runTask(null, () -> {
            papi(placeholders.parse(this.commands), player).forEach(command -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            });
        });
    }
}
