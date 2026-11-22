package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.zcore.logger.Logger;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.SwitchCaseButton;
import fr.maxlego08.menu.button.buttons.ZSwitchButton;
import fr.maxlego08.menu.loader.ZButtonLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
public class SwitchLoader extends ButtonLoader {

    private final MenuPlugin plugin;

    public SwitchLoader(MenuPlugin plugin) {
        super(plugin, "SWITCH");
        this.plugin = plugin;
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {

        var placeholder = configuration.getString(path + "key");
        var switchCaseButtons = new ArrayList<SwitchCaseButton>();
        var loader = new ZButtonLoader(this.plugin, defaultButtonValue.getFile(), defaultButtonValue.getInventorySize(), defaultButtonValue.getMatrix());
        var section = configuration.getSection(path + "buttons.");

        if (section != null) {
            for (String key : section.getRoutesAsStrings(false)) {

                try {
                    var button = loader.load(configuration, path + "buttons." + key + ".", key, defaultButtonValue);
                    switchCaseButtons.add(new SwitchCaseButton(key, button));
                } catch (InventoryException exception) {
                    Logger.error(exception);
                }
            }
        }

        return new ZSwitchButton(placeholder, switchCaseButtons);
    }

    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
