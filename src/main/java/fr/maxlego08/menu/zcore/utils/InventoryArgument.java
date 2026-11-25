package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.entity.Player;

import java.util.List;

public class InventoryArgument extends ZUtils {

    private final CommandManager commandManager;
    private final List<TextTemplate> arguments;

    public InventoryArgument(CommandManager commandManager, List<String> arguments) {
        this.commandManager = commandManager;
        this.arguments = TextService.texts(arguments);
    }

    @Deprecated
    public void process(Player player) {
        this.process(player, new Placeholders());
    }

    public void process(Player player, Placeholders placeholders){
        if (!this.arguments.isEmpty()) {
            for (int i = 0; i < this.arguments.size(); i++) {
                String name = String.valueOf(i - 4);
                String argument = this.arguments.get(i).render(placeholders.values(), player);

                if (argument.contains(":")) {
                    String[] values = argument.split(":", 2);
                    name = values[0];
                    argument = values[1];
                }

                this.commandManager.setPlayerArgument(player, name, argument);
            }
        }
    }

    public List<String> getArguments() {
        return TextService.sources(this.arguments);
    }
}
