package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCommandAction extends Action {

    private final List<String> commands;
    private final boolean inChat;

    public PlayerCommandAction(List<String> commands, boolean inChat) {
        this.commands = commands;
        this.inChat = inChat;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        ZScheduler scheduler = inventory.getPlugin().getScheduler();
        scheduler.runTask(player.getLocation(), () -> papi(placeholders.parse(this.parseAndFlattenCommands(this.commands, player)), player, true).forEach(command -> {
            command = command.replace("%player%", player.getName());
            if (this.inChat) {
                player.chat("/" + command);
            } else {
                Bukkit.dispatchCommand(player, command);
            }
        }));
    }
}
