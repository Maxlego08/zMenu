package fr.maxlego08.menu.hooks.bedrock.loader.input;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockLabelLoader extends ButtonLoader {

    public BedrockLabelLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BedrockLabelLoader(Plugin plugin) {
        super(plugin, "bedrock_label");
    }

    @Override
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        InputButton inputButton = new InputButton();
        String label = configuration.getString(path + ".text", "");

        inputButton.setLabel(label);
        inputButton.setInputType(DialogInputType.BEDROCK_LABEL);

        return inputButton;
    }
}
