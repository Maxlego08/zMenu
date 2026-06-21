package fr.maxlego08.menu.hooks.bedrock.button;

import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import org.geysermc.cumulus.component.Component;
import org.jetbrains.annotations.NotNull;

public class ExpandedBedrockInputButton extends BedrockInputButton {
    private final BedrockInputButton original;
    private final String key;

    public ExpandedBedrockInputButton(BedrockInputButton original, String key) {
        this.original = original;
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        return this.original.build(context);
    }
}
