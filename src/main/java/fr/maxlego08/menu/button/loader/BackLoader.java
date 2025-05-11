package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZBackButton;
import org.bukkit.configuration.file.YamlConfiguration;

public class BackLoader extends ButtonLoader {

    private final InventoryManager manager;

    public BackLoader(MenuPlugin plugin) {
        super(plugin, "back");
        this.manager = plugin.getInventoryManager();
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        return new ZBackButton(this.manager);
    }
}
