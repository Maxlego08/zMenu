package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerCommandAction extends ActionHelper {

    private final List<String> commands;
    private final boolean inChat;

    public PlayerCommandAction(List<String> commands, boolean inChat) {
        this.commands = commands;
        this.inChat = inChat;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        var scheduler = inventory.getPlugin().getScheduler();
        scheduler.runAtEntity(player, w -> papi(placeholders.parse(this.parseAndFlattenCommands(this.commands, player)), player).forEach(command -> {
            command = command.replace("%player%", player.getName());
            if (this.inChat) {
                player.chat("/" + command);
            } else {
                Bukkit.dispatchCommand(player, command);
            }
        }));
    }
}
