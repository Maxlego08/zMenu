package fr.maxlego08.menu.api.button.buttons.bedrock.components;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.jetbrains.annotations.Nullable;

public abstract class BedrockComponentButton extends Button {
    private String text;
    private BedrockImageType imageType = BedrockImageType.NONE;
    private String imageData;


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

    public void onRender(BedrockRenderContext<ButtonComponent> context) {
    }

    @Nullable
    public ButtonComponent build(BedrockRenderContext<ButtonComponent> context) {
        return null;
    }
}