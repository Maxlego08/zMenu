package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.TextTemplate;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockSliderInput extends VanillaBedrockInput {
    private final float start;
    private final float end;
    private final float step;
    private final TextTemplate initialValueTemplate;

    public BedrockSliderInput(@NotNull String text, float start, float end, float step,@NotNull String initialValue) {
        super(BedrockComponentType.SLIDER, text);
        this.start = start;
        this.end = end;
        this.step = step;
        this.initialValueTemplate = TextService.get().flow().compileText(initialValue);
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MenuPlugin menuPlugin = context.getPlugin();

        float start = this.start;
        float end = this.end;
        float step = this.step;
        String initialValue = this.initialValueTemplate.render(placeholders.values(), player);
        float initialValueFloat;
        try {
            initialValueFloat = Float.parseFloat(initialValue);
        } catch (NumberFormatException e) {
            initialValueFloat = (start + end) / 2;
        }
        if (initialValueFloat > end || initialValueFloat < start) {
            if (Configuration.enableDebug) {
                menuPlugin.getLogger().info("The initial value of the slider input is out of bounds. Start: " + start + ", End: " + end + ", Initial Value: " + initialValueFloat + ". Setting to middle value.");
            }
            initialValueFloat = (start + end) / 2;
        }
        return SliderComponent.of(this.legacyText(context), start, end, step, initialValueFloat);
    }


}