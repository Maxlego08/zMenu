package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import org.geysermc.cumulus.component.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class BedrockInputButton extends Button {
    protected String key;

    @Contract(pure = true)
    @NotNull
    public String getKey() {
        return this.key;
    }

    @Contract("_ -> this")
    public BedrockInputButton setKey(@NotNull String key) {
        this.key = key;
        return this;
    }

    public Component build(@NotNull BedrockRenderContext context) {
        return null;
    }

    public void onRender(@NotNull BedrockRenderContext context) {

    }
}
