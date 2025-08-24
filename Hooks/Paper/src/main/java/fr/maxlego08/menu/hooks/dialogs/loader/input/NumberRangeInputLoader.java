package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.loader.InputLoader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class NumberRangeInputLoader implements InputLoader {
    @Override
    public String getKey() {
        return "number_range";
    }

    @Override
    public DialogInputType getInputType() {
        return DialogInputType.NUMBER_RANGE;
    }

    @Override
    public InputButton load(String path, File file, YamlConfiguration configuration) {
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

        return inputButton;
    }
}
