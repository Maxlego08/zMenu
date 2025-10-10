package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.ZBedrockButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockModalButtonLoader extends ButtonLoader {

    public BedrockModalButtonLoader(Plugin plugin) {
        super(plugin, "bedrock_modal_button");
    }

    @Override
    public BedrockButton load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        String text = configuration.getString(path + ".text", "");

        return new ZBedrockButton(text);
    }
}
