package fr.maxlego08.menu.hooks.dialogs.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogBooleanInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class DialogBooleanInputLoader extends ButtonLoader {

    public DialogBooleanInputLoader(Plugin plugin) {
        super(plugin, "dialog_boolean");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".label", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));
        String textTrue = configuration.getString(path + ".text-true", "True");
        String textFalse = configuration.getString(path + ".text-false", "False");

        return new ZDialogBooleanInput(label, defaultValue, textTrue, textFalse);
    }
}
