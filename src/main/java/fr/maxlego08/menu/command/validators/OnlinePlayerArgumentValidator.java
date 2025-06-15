package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.plugin.Plugin;

public class OnlinePlayerArgumentValidator implements CommandArgumentValidator {

    private final Plugin plugin;

    public OnlinePlayerArgumentValidator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isValid(String value) {
        return plugin.getServer().getPlayerExact(value) != null;
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_ONLINE_PLAYER;
    }

    @Override
    public String getType() {
        return "online-player";
    }
}
