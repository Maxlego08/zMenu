package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZInventoryButton;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class InventoryLoader extends ButtonLoader {

    private final InventoryManager manager;
    private final CommandManager commandManager;

    public InventoryLoader(MenuPlugin plugin) {
        super(plugin, "inventory");
        this.manager = plugin.getInventoryManager();
        this.commandManager = plugin.getCommandManager();
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        String inventoryName = configuration.getString(path + "inventory");
        String pluginName = configuration.getString(path + "plugin");
        int toPage = configuration.getInt(path + "toPage", configuration.getInt(path + "to-page", 1));
        List<String> arguments = configuration.getStringList(path + "arguments");
        return new ZInventoryButton(this.manager, this.commandManager, inventoryName, pluginName, arguments, toPage);
    }
}
