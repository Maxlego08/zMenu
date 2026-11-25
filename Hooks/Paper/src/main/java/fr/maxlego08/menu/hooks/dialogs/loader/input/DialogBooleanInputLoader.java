package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.inputs.DialogBooleanInput;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogBooleanInputLoader extends ButtonLoader {

    public DialogBooleanInputLoader(MenuPlugin plugin) {
        super(plugin, "dialog_boolean");
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".label", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));
        String textTrue = configuration.getString(path + ".text-true", "True");
        String textFalse = configuration.getString(path + ".text-false", "False");

        return new DialogBooleanInput(label, defaultValue, textTrue, textFalse);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
