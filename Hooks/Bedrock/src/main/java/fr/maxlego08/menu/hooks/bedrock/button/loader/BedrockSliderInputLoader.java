package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockSliderInputLoader extends ButtonLoader {

    public BedrockSliderInputLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BedrockSliderInputLoader(Plugin plugin) {
        super(plugin, "bedrock_slider");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".text", "");
        float start = (float) configuration.getDouble(path + ".start", 0);
        float end = (float) configuration.getDouble(path + ".end", 100);
        float step = (float) configuration.getDouble(path + ".step", 1);
        String initialValue = configuration.getString(path + ".initial-value", String.valueOf((end + start) / 2));

        inputButton.setLabel(label);
        inputButton.setStart(start);
        inputButton.setEnd(end);
        inputButton.setStep(step);
        inputButton.setInitialValueRange(initialValue);
        inputButton.setInputType(DialogInputType.NUMBER_RANGE);

        return inputButton;
    }
}
