package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockToggleInput;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockToggleInputLoader extends ButtonLoader {

    public BedrockToggleInputLoader(MenuPlugin plugin) {
        super(plugin, "bedrock_toggle");
    }

    @Override
    public Button load(@NonNull YamlDocument configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String text = configuration.getString(path + ".text", "");
        String defaultValue = configuration.getString(path + ".initial-value", String.valueOf(true));

        return new BedrockToggleInput(text, defaultValue);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public Button load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path, defaultButtonValue);
    }
}
