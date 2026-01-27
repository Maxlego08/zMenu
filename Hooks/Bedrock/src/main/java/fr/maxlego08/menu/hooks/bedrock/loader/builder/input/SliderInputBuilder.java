package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;

public class SliderInputBuilder implements BedrockBuilderInput {
    private final MenuPlugin menuPlugin;

    public SliderInputBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.NUMBER_RANGE;
    }

    @Override
    public Component build(Player player, InputButton button) {

        String text = this.menuPlugin.parse(player, button.getLabel());
        float start = button.getStart();
        float end = button.getEnd();

        float initialValueFloat;
        String initialValue = this.menuPlugin.parse(player, button.getInitialValueRange());
        try {
            initialValueFloat = Float.parseFloat(initialValue);
        } catch (NumberFormatException e) {
            initialValueFloat = (start + end) / 2;
        }

        float step = button.getStep();
        if (initialValueFloat>end || initialValueFloat<start) {
            if (Config.enableInformationMessage){
                Logger.info("The initial value of the number range input is out of bounds. Start: " + start + ", End: " + end + ", Initial Value: " + initialValueFloat + ". Setting to middle value.");
            }
            initialValueFloat = (start + end) / 2;
        }

        return SliderComponent.of(text, start, end, step, initialValueFloat);
    }
}
