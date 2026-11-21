package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.ToggleComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockToggleInput extends VanillaBedrockInput {
    private final TextTemplate defaultValueTemplate;

    public BedrockToggleInput(@NotNull String text, @NotNull String defaultValue) {
        super(BedrockComponentType.TOGGLE, text);
        this.defaultValueTemplate = TextService.get().flow().compileText(defaultValue);
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();

        boolean defaultValue = Boolean.parseBoolean(this.defaultValueTemplate.render(placeholders.values(), player));
        return ToggleComponent.of(this.legacyText(context), defaultValue);
    }
}