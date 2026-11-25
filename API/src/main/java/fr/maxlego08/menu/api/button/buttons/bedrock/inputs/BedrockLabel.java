package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockLabel extends VanillaBedrockInput {

    public BedrockLabel(@NotNull String text) {
        super(BedrockComponentType.LABEL, text);
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        return LabelComponent.of(this.legacyText(context));
    }
}