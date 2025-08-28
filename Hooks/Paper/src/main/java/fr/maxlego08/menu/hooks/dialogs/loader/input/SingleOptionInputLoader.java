package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class SingleOptionInputLoader extends ButtonLoader {

    public SingleOptionInputLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public SingleOptionInputLoader(Plugin plugin) {
        super(plugin, "dialog_single_option");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();

        String label = configuration.getString(path + ".label", "");
        boolean labelVisible = configuration.getBoolean(path + ".label-visible", true);
        List<SingleOption> singleOptionList = new ArrayList<>();

        if (configuration.isConfigurationSection(path + ".options")) {
            boolean initialAlreadySet = false;
            for (String optionKey : configuration.getConfigurationSection(path + ".options").getKeys(false)) {
                String optionPath = path + ".options." + optionKey;

                String id = configuration.getString(optionPath + ".id", optionKey);
                String display = configuration.getString(optionPath + ".display", "");
                boolean initialValue = configuration.getBoolean(optionPath + ".initial", false);

                if (initialAlreadySet) {
                    initialValue = false;
                } else if (initialValue) {
                    initialAlreadySet = true;
                }

                SingleOption singleOption = new SingleOption(id, display, initialValue);
                singleOptionList.add(singleOption);
            }
        }

        inputButton.setLabel(label);
        inputButton.setLabelVisible(labelVisible);
        inputButton.setSigleOptions(singleOptionList);
        inputButton.setInputType(DialogInputType.SINGLE_OPTION);
        return inputButton;
    }
}
