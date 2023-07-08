package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.BackButton;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZBackButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BackLoader implements ButtonLoader {

    private final Plugin plugin;
    private final InventoryManager manager;

    /**
     * @param plugin
     * @param manager
     */
    public BackLoader(Plugin plugin, InventoryManager manager) {
        super();
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public Class<? extends Button> getButton() {
        return BackButton.class;
    }

    @Override
    public String getName() {
        return "back";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path) {
        return new ZBackButton(this.manager);
    }

}
