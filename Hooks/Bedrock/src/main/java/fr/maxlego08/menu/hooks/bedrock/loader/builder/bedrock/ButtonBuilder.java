package fr.maxlego08.menu.hooks.bedrock.loader.builder.bedrock;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.bedrock.BedrockButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

public class ButtonBuilder {
    private final MenuPlugin menuPlugin;

    public ButtonBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    public ButtonComponent build(Player player, BedrockButton button) {
        String text = menuPlugin.parse(player, button.getText(player));

        BedrockImageType imageType = button.getImageType(player);
        if (imageType == BedrockImageType.NONE){
            return ButtonComponent.of(text);
        }
        return ButtonComponent.of(text, FormImage.Type.valueOf(imageType.toString()), button.getImageData(player));
    }
}