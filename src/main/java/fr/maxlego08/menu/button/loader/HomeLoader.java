package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZHomeButton;
import org.bukkit.configuration.file.YamlConfiguration;

public class HomeLoader extends ButtonLoader {

    private final InventoryManager manager;

    public HomeLoader(MenuPlugin plugin) {
        super(plugin, "home");
        this.manager = plugin.getInventoryManager();
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        return new ZHomeButton(this.manager);
    }

}
