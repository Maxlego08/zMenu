package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.InputLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.record.SingleOption;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SingleOptionInputLoader implements InputLoader {
    @Override
    public String getKey() {
        return "single_option";
    }

    @Override
    public DialogInputType getInputType() {
        return DialogInputType.SINGLE_OPTION;
    }

    @Override
    public InputButton load(String path, File file, YamlConfiguration configuration) {
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
        inputButton.setOptions(singleOptionList);

        return inputButton;
    }
}
