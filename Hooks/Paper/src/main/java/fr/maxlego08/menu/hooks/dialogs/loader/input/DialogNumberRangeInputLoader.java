package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.dialogs.inputs.DialogNumberRangeInput;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogNumberRangeInputLoader extends ButtonLoader {

    public DialogNumberRangeInputLoader(MenuPlugin plugin) {
        super(plugin, "dialog_number_range");
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".label", "");
        int width = configuration.getInt(path + ".width", 200);
        float start = (float) (double) configuration.getDouble(path + ".start", 0.0);
        float end = (float) (double) configuration.getDouble(path + ".end", 100.0);
        float step = (float) (double) configuration.getDouble(path + ".step", 1.0);
        String initialValue = configuration.getString(path + ".initial-value", String.valueOf((end + start) / 2));
        String labelFormat = configuration.getString(path + ".label-format", "options.generic_value");

        return new DialogNumberRangeInput(label, width, labelFormat, start, end, step, initialValue);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
