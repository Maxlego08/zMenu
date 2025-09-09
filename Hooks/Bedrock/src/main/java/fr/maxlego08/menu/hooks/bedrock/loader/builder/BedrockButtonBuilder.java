package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

public class BedrockButtonBuilder {
    private final MenuPlugin menuPlugin;

    public BedrockButtonBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    public ButtonComponent build(Player player, BedrockButton button) {
        String text = menuPlugin.parse(player, button.getText());

        BedrockImageType imageType = button.getImageType();
        if (imageType == BedrockImageType.NONE){
            return ButtonComponent.of(text);
        }
        return ButtonComponent.of(text, FormImage.Type.valueOf(imageType.toString()), button.getImageData());
    }
}