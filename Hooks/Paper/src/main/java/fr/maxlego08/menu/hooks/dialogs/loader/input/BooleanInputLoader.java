package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.InputLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BooleanInputLoader implements InputLoader {

    @Override
    public String getKey() {
        return "boolean";
    }

    @Override
    public DialogInputType getInputType() {
        return DialogInputType.BOOLEAN;
    }

    @Override
    public InputButton load(String path, File file, YamlConfiguration configuration) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".label", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));
        String textTrue = configuration.getString(path + ".text-true", "True");
        String textFalse = configuration.getString(path + ".text-false", "False");

        inputButton.setLabel(label);
        inputButton.setInitialValueBool(defaultValue);
        inputButton.setTextTrue(textTrue);
        inputButton.setTextFalse(textFalse);

        return inputButton;
    }
}
