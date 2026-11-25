package fr.maxlego08.menu.api.button.buttons.bedrock.components;

import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BasicBedrockComponentButton extends BedrockComponentButton {
    private final Template textTemplate;
    private final BedrockImageType imageType;
    /**
     * The image's path or URL, read as text: its placeholders only, never colour codes or font images.
     */
    private final TextTemplate imageDataTemplate;

    public BasicBedrockComponentButton(@NotNull String text) {
        this(text, BedrockImageType.NONE, null);
    }

    public BasicBedrockComponentButton(@NotNull String text, @NotNull BedrockImageType imageType, @Nullable String imageData) {
        Objects.requireNonNull(text, "text cannot be null");
        Objects.requireNonNull(imageType, "imageType cannot be null");
        this.textTemplate = TextService.get().flow().compile(text);
        this.imageType = imageType;
        this.imageDataTemplate = imageData != null ? TextService.get().flow().compileText(imageData) : null;
    }

    /**
     * @return the button's text, compiled once
     */
    public @NotNull Template getTextTemplate() {
        return this.textTemplate;
    }

    public @NotNull String getText() {
        return this.textTemplate.source();
    }

    @Override
    public ButtonComponent build(BedrockRenderContext<ButtonComponent> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();

        String parsedText = TextService.legacy(this.textTemplate, placeholders, player);
        if (this.imageType != BedrockImageType.NONE && this.imageDataTemplate != null) {
            String parsedImageData = this.imageDataTemplate.render(placeholders.values(), player);
            return ButtonComponent.of(parsedText, FormImage.Type.valueOf(this.imageType.toString()), parsedImageData);
        }
        return ButtonComponent.of(parsedText);
    }
}
