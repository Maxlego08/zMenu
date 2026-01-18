package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class InventoryArgument extends ZUtils {

    private final CommandManager commandManager;
    private final List<String> arguments;

    public InventoryArgument(CommandManager commandManager, List<String> arguments) {
        this.commandManager = commandManager;
        this.arguments = arguments;
    }

    @Deprecated
    public void process(Player player) {
        process(player, new Placeholders());
    }

    public void process(Player player, Placeholders placeholders){
        if (!this.arguments.isEmpty()) {
            for (int i = 0; i < this.arguments.size(); i++) {
                String name = String.valueOf(i - 4);
                String argument = papi(placeholders.parse(this.arguments.get(i)), player, true);

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
