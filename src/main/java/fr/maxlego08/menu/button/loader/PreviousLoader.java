package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZPreviousButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class PreviousLoader implements ButtonLoader {

    private final Plugin plugin;
    private final InventoryManager manager;

    public PreviousLoader(Plugin plugin, InventoryManager manager) {
        super();
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public Class<? extends Button> getButton() {
        return fr.maxlego08.menu.api.button.buttons.PreviousButton.class;
    }

    @Override
    public String getName() {
        return "previous";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        defaultButtonValue.setPermanent(true);
        return new ZPreviousButton(this.manager);
    }

}
