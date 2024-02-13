package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.InventoryButton;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZInventoryButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class InventoryLoader implements ButtonLoader {

    private final Plugin plugin;
    private final InventoryManager manager;
    private final CommandManager commandManager;

    public InventoryLoader(Plugin plugin, InventoryManager manager, CommandManager commandManager) {
        super();
        this.plugin = plugin;
        this.manager = manager;
        this.commandManager = commandManager;
    }

    @Override
    public Class<? extends Button> getButton() {
        return InventoryButton.class;
    }

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        String inventoryName = configuration.getString(path + "inventory");
        String pluginName = configuration.getString(path + "plugin");
        int toPage = configuration.getInt(path + "toPage", 1);
        List<String> arguments = configuration.getStringList(path + "arguments");
        return new ZInventoryButton(this.manager, this.commandManager, inventoryName, pluginName, arguments, toPage);
    }

}
