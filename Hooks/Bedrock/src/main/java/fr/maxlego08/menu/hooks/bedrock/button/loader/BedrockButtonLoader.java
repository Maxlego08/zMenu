package fr.maxlego08.menu.hooks.bedrock.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.ZBedrockButton;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockButtonLoader extends ButtonLoader {

    public BedrockButtonLoader(MenuPlugin plugin) {
        super(plugin, "bedrock_button");
    }

    @Override
    public BedrockButton load(@NonNull YamlConfiguration configuration, @NonNull String path, @NonNull DefaultButtonValue defaultButtonValue) {
        String text = configuration.getString(path + ".text", "");

        // Type d'image
        BedrockImageType imageType;
        String imageTypeStr = configuration.getString(path + ".image-type", "");
        try {
            imageType = BedrockImageType.valueOf(imageTypeStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            imageType = BedrockImageType.NONE;
        }

        // Données de l'image
        String imageData = configuration.getString(path + ".image-value", "");

        return new ZBedrockButton(text, imageType, imageData);
    }
}
