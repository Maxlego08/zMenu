package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZHomeButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class HomeLoader implements ButtonLoader {

    private final Plugin plugin;
    private final InventoryManager manager;

    /**
     * @param plugin
     * @param manager
     */
    public HomeLoader(Plugin plugin, InventoryManager manager) {
        super();
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public Class<? extends Button> getButton() {
        return HomeButton.class;
    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        return new ZHomeButton(this.manager);
    }

}
