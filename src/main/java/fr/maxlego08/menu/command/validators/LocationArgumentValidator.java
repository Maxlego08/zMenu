package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class LocationArgumentValidator implements CommandArgumentValidator {

    private final Plugin plugin;

    public LocationArgumentValidator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isValid(String value) {
        try {
            String[] parts = value.split(",");
            if (parts.length != 4) return false;

            World world = plugin.getServer().getWorld(parts[0]);
            if (world == null) return false;

            Double.parseDouble(parts[1]);
            Double.parseDouble(parts[2]);
            Double.parseDouble(parts[3]);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_LOCATION;
    }
}
