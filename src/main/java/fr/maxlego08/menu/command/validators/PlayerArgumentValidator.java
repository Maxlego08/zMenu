package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.plugin.Plugin;

public class PlayerArgumentValidator implements CommandArgumentValidator {

    private final Plugin plugin;

    public PlayerArgumentValidator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isValid(String value) {
        return this.plugin.getServer().getOfflinePlayer(value).hasPlayedBefore();
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_PLAYER;
    }
}
