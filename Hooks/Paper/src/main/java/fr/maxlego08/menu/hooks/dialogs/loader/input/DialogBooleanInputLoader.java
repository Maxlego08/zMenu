package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogBooleanInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogBooleanInputLoader extends ButtonLoader {

    public DialogBooleanInputLoader(MenuPlugin plugin) {
        super(plugin, "dialog_boolean");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".label", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));
        String textTrue = configuration.getString(path + ".text-true", "True");
        String textFalse = configuration.getString(path + ".text-false", "False");

        return new ZDialogBooleanInput(label, defaultValue, textTrue, textFalse);
    }
}
