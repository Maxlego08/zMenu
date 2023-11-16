package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ConsoleCommandAction extends ZUtils implements Action {

    private final List<String> commands;

    public ConsoleCommandAction(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(Player player) {
        papi(this.commands, player).forEach(command -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        });
    }
}
