package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.annotations.AutoCommandArgumentValidator;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.plugin.Plugin;

@AutoCommandArgumentValidator
public class PlayerArgumentValidator implements CommandArgumentValidator {

    private final Plugin plugin;

    public PlayerArgumentValidator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isValid(String value) {
        if (value.equalsIgnoreCase("%player%")) return true;
        return this.plugin.getServer().getOfflinePlayer(value).hasPlayedBefore();
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_PLAYER;
    }

    @Override
    public String getType() {
        return "player";
    }
}
