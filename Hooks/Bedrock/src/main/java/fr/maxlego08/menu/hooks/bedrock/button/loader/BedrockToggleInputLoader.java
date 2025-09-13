package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockToggleInputLoader extends ButtonLoader {

    public BedrockToggleInputLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BedrockToggleInputLoader(Plugin plugin) {
        super(plugin, "bedrock_toggle");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".text", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));

        inputButton.setLabel(label);
        inputButton.setInitialValueBool(defaultValue);
        inputButton.setInputType(DialogInputType.BOOLEAN);

        return inputButton;
    }
}
