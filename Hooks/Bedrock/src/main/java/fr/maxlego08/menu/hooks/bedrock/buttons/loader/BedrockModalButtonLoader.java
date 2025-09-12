package fr.maxlego08.menu.hooks.bedrock.buttons.loader;

import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockModalButtonLoader extends ButtonLoader {

    public BedrockModalButtonLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BedrockModalButtonLoader(Plugin plugin) {
        super(plugin, "bedrock_modal_button");
    }

    @Override
    public BedrockButton load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        BedrockButton bedrockButton = new BedrockButton();
        String text = configuration.getString(path + ".text", "");

        bedrockButton.setText(text);
        return bedrockButton;
    }
}
