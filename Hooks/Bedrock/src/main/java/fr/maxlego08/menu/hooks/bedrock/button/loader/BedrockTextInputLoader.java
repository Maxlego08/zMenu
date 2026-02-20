package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.ZBedrockTextInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

public class BedrockTextInputLoader extends ButtonLoader {

    public BedrockTextInputLoader(Plugin plugin) {
        super(plugin, "bedrock_text");
    }

    @Override
    public Button load(YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String text = configuration.getString(path + ".text", "");
        String defaultValue = configuration.getString(path + ".default-value", "");
        return new ZBedrockTextInput(text, defaultValue);
    }
}
