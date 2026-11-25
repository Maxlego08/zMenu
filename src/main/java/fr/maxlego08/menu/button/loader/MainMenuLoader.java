package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.ZMainMenuButton;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
public class MainMenuLoader extends ButtonLoader {

    private final InventoryManager manager;

    public MainMenuLoader(MenuPlugin plugin) {
        super(plugin, "mainmenu");
        this.manager = plugin.getInventoryManager();
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        return new ZMainMenuButton(this.manager);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
