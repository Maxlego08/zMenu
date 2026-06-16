package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.dialogs.button.buttons.ZDialogNumberRangeInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogNumberRangeInputLoader extends ButtonLoader {

    public DialogNumberRangeInputLoader(MenuPlugin plugin) {
        super(plugin, "dialog_number_range");
    }

    @Override
    public Button load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String label = configuration.getString(path + ".label", "");
        int width = configuration.getInt(path + ".width", 200);
        float start = (float) configuration.getDouble(path + ".start", 0);
        float end = (float) configuration.getDouble(path + ".end", 100);
        float step = (float) configuration.getDouble(path + ".step", 1);
        String initialValue = configuration.getString(path + ".initial-value", String.valueOf((end + start) / 2));
        String labelFormat = configuration.getString(path + ".label-format", "options.generic_value");

        return new ZDialogNumberRangeInput(label, start, end, step, initialValue, width, labelFormat);
    }
}
