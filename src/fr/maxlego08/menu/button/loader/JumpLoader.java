package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZJumpButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class JumpLoader implements ButtonLoader {
    private Plugin plugin;
    private InventoryManager inventoryManager;

    public JumpLoader(Plugin plugin, InventoryManager inventoryManager) {
        this.plugin = plugin;
        this.inventoryManager = inventoryManager;
    }

    @Override
    public Class<? extends Button> getButton() {
        return ZJumpButton.class;
    }

    @Override
    public String getName() {
        return "jump";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        int page = configuration.getInt("page");
        return new ZJumpButton(this.inventoryManager, page);
    }
}
