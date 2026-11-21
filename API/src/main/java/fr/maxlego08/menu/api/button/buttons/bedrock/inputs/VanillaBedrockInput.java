package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.robie.minimessageflow.Template;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import org.jetbrains.annotations.NotNull;

public abstract class VanillaBedrockInput extends BedrockInputButton {
    private final BedrockComponentType type;
    protected final String text;
    /**
     * {@link #text} compiled once.
     */
    private final transient Template textTemplate;

    protected VanillaBedrockInput(@NotNull BedrockComponentType type, @NotNull String text) {
        this.type = type;
        this.text = text;
        this.textTemplate = TextService.get().flow().compile(text);
    }

    /**
     * The text rendered for the form's player with its values, as legacy text.
     */
    protected @NotNull String legacyText(@NotNull BedrockRenderContext<?> context) {
        return TextService.legacy(this.textTemplate, context.getPlaceholders(), context.getPlayer());
    }

    public @NotNull BedrockComponentType getType() {
        return this.type;
    }
}
