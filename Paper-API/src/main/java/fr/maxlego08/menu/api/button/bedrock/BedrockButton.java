package fr.maxlego08.menu.api.button.bedrock;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

public class BedrockButton extends Button {
    private String text;
    private BedrockImageType imageType = BedrockImageType.NONE;
    private String imageData;

    public BedrockButton(String text) {
        super();
        this.text = text;
    }

    public BedrockButton(String text, BedrockImageType imageType, String imageData) {
        super();
        this.text = text;
        this.imageType = imageType;
        this.imageData = imageData;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(Placeholders placeholders) {
        return placeholders.parse(this.text);
    }

    public String getRawText() {
        return this.text;
    }

    public void setImageType(BedrockImageType imageType) {
        this.imageType = imageType;
    }

    public BedrockImageType getImageType(Player player) {
        return this.imageType;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageData() {
        return this.imageData;
    }
}