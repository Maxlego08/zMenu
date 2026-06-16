package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockSliderInput extends VanillaBedrockInput {
    private final float start;
    private final float end;
    private final float step;
    private final String initialValue;

    public BedrockSliderInput(@NotNull String text, float start, float end, float step,@NotNull String initialValue) {
        super(BedrockComponentType.SLIDER, text);
        this.start = start;
        this.end = end;
        this.step = step;
        this.initialValue = initialValue;
    }

    public Component build(@NotNull BedrockRenderContext context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin menuPlugin = context.getPlugin();

        String text = menuPlugin.parse(player, placeholders.parse(this.text));
        float start = this.start;
        float end = this.end;
        float step = this.step;
        String initialValue = menuPlugin.parse(player, placeholders.parse(this.initialValue));
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
        return SliderComponent.of(metaUpdater.getLegacyMessage(text), start, end, step, initialValueFloat);
    }


}