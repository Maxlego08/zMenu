package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.SliderComponent;
import org.jetbrains.annotations.NotNull;

public class SliderInputBuilder extends BedrockBuilderInput<MenuPlugin> {

    public SliderInputBuilder(MenuPlugin menuPlugin) {
        super(menuPlugin, menuPlugin.getMetaUpdater());
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.NUMBER_RANGE;
    }

    @Override
    public @NotNull Component build(@NotNull Player player, @NotNull InputButton button, @NotNull Placeholders placeholders) {

        String text = this.plugin.parse(player, placeholders.parse(button.getLabel()));
        float start = button.getStart();
        float end = button.getEnd();

        float initialValueFloat;
        String initialValue = this.plugin.parse(player, placeholders.parse(button.getInitialValueRange()));
        try {
            initialValueFloat = Float.parseFloat(initialValue);
        } catch (NumberFormatException e) {
            initialValueFloat = (start + end) / 2;
        }

        float step = button.getStep();
        if (initialValueFloat>end || initialValueFloat<start) {
            if (Configuration.enableInformationMessage){
                Logger.info("The initial value of the number range input is out of bounds. Start: " + start + ", End: " + end + ", Initial Value: " + initialValueFloat + ". Setting to middle value.");
            }
            initialValueFloat = (start + end) / 2;
        }

        return SliderComponent.of(this.metaUpdater.getLegacyMessage(text), start, end, step, initialValueFloat);
    }
}
