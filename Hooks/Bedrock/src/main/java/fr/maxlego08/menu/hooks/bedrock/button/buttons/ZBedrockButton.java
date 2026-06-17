package fr.maxlego08.menu.hooks.bedrock.button.buttons;

import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;

public class ZBedrockButton extends BedrockComponentButton {

    public ZBedrockButton(String text, BedrockImageType imageType, String imageData) {
        this.setText(text);
        this.setImageType(imageType);
        this.setImageData(imageData);
    }

    public ZBedrockButton(String text) {
        this.setText(text);
    }
}
