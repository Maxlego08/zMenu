package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.SwitchCaseButton;
import fr.maxlego08.menu.button.buttons.ZSwitchButton;
import fr.maxlego08.menu.loader.ZButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;

public class SwitchLoader extends ButtonLoader {

    private final MenuPlugin plugin;

    public SwitchLoader(MenuPlugin plugin) {
        super(plugin, "SWITCH");
        this.plugin = plugin;
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {

        var placeholder = configuration.getString(path + "key");
        var switchCaseButtons = new ArrayList<SwitchCaseButton>();
        var loader = new ZButtonLoader(this.plugin, defaultButtonValue.getFile(), defaultButtonValue.getInventorySize(), defaultButtonValue.getMatrix());
        var section = configuration.getConfigurationSection(path + "buttons.");

        if (section != null) {
            for (String key : section.getKeys(false)) {

                try {
                    var button = loader.load(configuration, path + "buttons." + key + ".", key, defaultButtonValue);
                    switchCaseButtons.add(new SwitchCaseButton(key, button));
                } catch (InventoryException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return new ZSwitchButton(placeholder, switchCaseButtons);
    }
}
