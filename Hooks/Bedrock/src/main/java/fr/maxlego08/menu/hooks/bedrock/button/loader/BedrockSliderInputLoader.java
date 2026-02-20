package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.ZBedrockSliderInput;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

public class BedrockSliderInputLoader extends ButtonLoader {

    public BedrockSliderInputLoader(Plugin plugin) {
        super(plugin, "bedrock_slider");
    }

    @Override
    public Button load(YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String text = configuration.getString(path + ".text", "");
        float start = (float) configuration.getDouble(path + ".start", 0);
        float end = (float) configuration.getDouble(path + ".end", 100);
        float step = (float) configuration.getDouble(path + ".step", 1);
        String initialValue = configuration.getString(path + ".initial-value", String.valueOf((end + start) / 2));

        return new ZBedrockSliderInput(text, start, end, step, initialValue);
    }
}
