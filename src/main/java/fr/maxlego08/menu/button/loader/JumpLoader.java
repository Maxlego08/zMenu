package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZJumpButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

public class JumpLoader extends ButtonLoader {

    private final InventoryManager inventoryManager;

    public JumpLoader(MenuPlugin plugin) {
        super(plugin, "jump");
        this.inventoryManager = plugin.getInventoryManager();
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        int page = configuration.getInt(path + "toPage", configuration.getInt(path + "to-page"));
        return new ZJumpButton(this.inventoryManager, page);
    }
}
