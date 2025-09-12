package fr.maxlego08.menu.hooks.bedrock.buttons.loader;

import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BedrockButtonLoader extends ButtonLoader {

    public BedrockButtonLoader(Plugin plugin, String  name) {
        super(plugin, name);
    }

    public BedrockButtonLoader(Plugin plugin) {
        super(plugin, "bedrock_button");
    }

    @Override
    public BedrockButton load(YamlConfiguration configuration, String path, DefaultButtonValue defaultButtonValue) {
        BedrockButton bedrockButton = new BedrockButton();
        String text = configuration.getString(path + ".text", "");

        // Type d'image
        BedrockImageType imageType;
        String imageTypeStr = configuration.getString(path + ".image-type", "");
        try {
            imageType = BedrockImageType.valueOf(imageTypeStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            imageType = BedrockImageType.NONE;
        }

        // Données de l'image
        String imageData = configuration.getString(path + ".image-value", "");

        bedrockButton.setText(text);
        bedrockButton.setImageType(imageType);
        bedrockButton.setImageData(imageData);

        return bedrockButton;
    }

}
