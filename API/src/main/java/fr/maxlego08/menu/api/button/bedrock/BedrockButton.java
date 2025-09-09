package fr.maxlego08.menu.api.button.bedrock;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;

public class BedrockButton extends Button {
    private String text;
    private BedrockImageType imageType = BedrockImageType.NONE;
    private String imageData;

    public void setText(String text){
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setImageType(BedrockImageType imageType) {
        this.imageType = imageType;
    }

    public BedrockImageType getImageType() {
        return this.imageType;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageData() {
        return this.imageData;
    }
}