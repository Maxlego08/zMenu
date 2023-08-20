package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZMainMenuButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MainMenuLoader implements ButtonLoader {
    private final Plugin plugin;
    private final InventoryManager manager;

    public MainMenuLoader(Plugin plugin, InventoryManager manager) {
        super();
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public Class<? extends Button> getButton() {
        return ZMainMenuButton.class;
    }

    @Override
    public String getName() {
        return "mainmenu";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path) {
        return new ZMainMenuButton(manager);
    }
}
