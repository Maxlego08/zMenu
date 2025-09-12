package fr.maxlego08.menu.hooks.bedrock.buttons.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockInputTextLoader extends ButtonLoader {

    public BedrockInputTextLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BedrockInputTextLoader(Plugin plugin) {
        super(plugin, "bedrock_text");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".text", "");
        String defaultValue = configuration.getString(path + ".default-value", "");
        inputButton.setLabel(label);
        inputButton.setDefaultText(defaultValue);
        inputButton.setInputType(DialogInputType.TEXT);
        return inputButton;
    }
}
