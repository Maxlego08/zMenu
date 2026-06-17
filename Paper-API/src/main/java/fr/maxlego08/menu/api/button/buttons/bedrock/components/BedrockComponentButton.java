package fr.maxlego08.menu.api.button.buttons.bedrock.components;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import org.geysermc.cumulus.component.ButtonComponent;
import org.jetbrains.annotations.Nullable;

public abstract class BedrockComponentButton extends Button {

    public void onRender(BedrockRenderContext<ButtonComponent> context) {
    }

    @Nullable
    public ButtonComponent build(BedrockRenderContext<ButtonComponent> context) {
        return null;
    }
}