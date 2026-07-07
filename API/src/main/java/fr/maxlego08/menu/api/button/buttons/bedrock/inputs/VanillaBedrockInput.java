package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import org.jetbrains.annotations.NotNull;

public abstract class VanillaBedrockInput extends BedrockInputButton {
    private final BedrockComponentType type;
    protected final String text;

    protected VanillaBedrockInput(@NotNull BedrockComponentType type, @NotNull String text) {
        this.type = type;
        this.text = text;
    }

    public @NotNull BedrockComponentType getType() {
        return this.type;
    }
}
