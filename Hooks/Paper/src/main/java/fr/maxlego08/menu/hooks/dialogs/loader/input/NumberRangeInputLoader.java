package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class NumberRangeInputLoader extends ButtonLoader {

    public NumberRangeInputLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public NumberRangeInputLoader(Plugin plugin) {
        super(plugin, "dialog_number_range");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".label", "");
        int width = configuration.getInt(path + ".width", 200);
        float start = (float) configuration.getDouble(path + ".start", 0);
        float end = (float) configuration.getDouble(path + ".end", 100);
        float step = (float) configuration.getDouble(path + ".step", 1);
        String initialValue = configuration.getString(path + ".initial-value", String.valueOf((end + start) / 2));
        String labelFormat = configuration.getString(path + ".label-format", "options.generic_value");

        inputButton.setLabel(label);
        inputButton.setWidth(width);
        inputButton.setStart(start);
        inputButton.setEnd(end);
        inputButton.setStep(step);
        inputButton.setLabelFormat(labelFormat);
        inputButton.setInitialValueRange(initialValue);
        inputButton.setInputType(DialogInputType.NUMBER_RANGE);

        return inputButton;
    }
}
