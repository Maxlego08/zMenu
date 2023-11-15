package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerChatAction extends ZUtils implements Action {

    private final List<String> commands;

    public PlayerChatAction(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(Player player) {
        papi(this.commands, player).forEach(command -> player.chat(command.replace("%player%", player.getName())));
    }
}
