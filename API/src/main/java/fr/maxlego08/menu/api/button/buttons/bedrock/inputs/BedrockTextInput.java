package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockTextInput extends VanillaBedrockInput {
    private final TextTemplate defaultTextTemplate;

    public BedrockTextInput(@NotNull String text, @NotNull String defaultText) {
        super(BedrockComponentType.INPUT, text);
        this.defaultTextTemplate = TextService.get().flow().compileText(defaultText);
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        return InputComponent.of(this.legacyText(context), this.defaultTextTemplate.render(placeholders.values(), player));
    }
}
