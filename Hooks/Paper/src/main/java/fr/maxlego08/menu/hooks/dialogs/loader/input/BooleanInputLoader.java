package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

public class BooleanInputLoader extends ButtonLoader {

    public BooleanInputLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BooleanInputLoader(Plugin plugin) {
        super(plugin, "dialog_boolean");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".label", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));
        String textTrue = configuration.getString(path + ".text-true", "True");
        String textFalse = configuration.getString(path + ".text-false", "False");

        inputButton.setLabel(label);
        inputButton.setInitialValueBool(defaultValue);
        inputButton.setTextTrue(textTrue);
        inputButton.setTextFalse(textFalse);
        inputButton.setInputType(DialogInputType.BOOLEAN);

        return inputButton;
    }
}
