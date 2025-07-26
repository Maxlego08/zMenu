package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.command.CommandManager;
import org.bukkit.entity.Player;

import java.util.List;

public class InventoryArgument extends ZUtils {

    private final CommandManager commandManager;
    private final List<String> arguments;

    public InventoryArgument(CommandManager commandManager, List<String> arguments) {
        this.commandManager = commandManager;
        this.arguments = arguments;
    }

    public void process(Player player) {
        if (!this.arguments.isEmpty()) {
            for (int i = 0; i < this.arguments.size(); i++) {
                String name = String.valueOf(i - 4);
                String argument = papi(this.arguments.get(i), player, true);

                if (argument.contains(":")) {
                    String[] values = argument.split(":", 2);
                    //name = values[0];
                    name = papi(values[0], player, true);
                    //argument = values[1];
                    argument = papi(values[1], player, true);
                }

                this.commandManager.setPlayerArgument(player, name, argument);
            }
        }
    }

    public List<String> getArguments() {
        return arguments;
    }
}
