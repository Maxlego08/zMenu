package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerChatAction extends Action {

    private final List<String> commands;

    public PlayerChatAction(List<String> commands) {
        this.commands = commands;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        papi(placeholders.parse(this.parseAndFlattenCommands(this.commands, player)), player, true).forEach(command -> player.chat(command.replace("%player%", player.getName())));
    }
}
