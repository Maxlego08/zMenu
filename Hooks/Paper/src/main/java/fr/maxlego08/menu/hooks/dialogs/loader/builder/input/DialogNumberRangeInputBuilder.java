package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DialogNumberRangeInputBuilder implements DialogInputBuilderInt {
    private final ZDialogManager dialogManager;
    private final MenuPlugin menuPlugin;

    public DialogNumberRangeInputBuilder(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.dialogManager = dialogManager;
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.NUMBER_RANGE;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();
        int width = button.getWidth();
        Component label = this.dialogManager.getPaperComponent().getComponent(this.menuPlugin.parse(player, button.getLabel()));
        String labelFormat = this.menuPlugin.parse(player, button.getLabelFormat());
        float start = button.getStart();
        float end = button.getEnd();
        Optional<Float> initialValueSupplier = button.getInitialValueRangeSupplier();
        float initialValueFloat;
        if (initialValueSupplier.isPresent()) {
             initialValueFloat = initialValueSupplier.get();
        } else {
            String initialValue = this.menuPlugin.parse(player, button.getInitialValueRange());
            try {
                initialValueFloat = Float.parseFloat(initialValue);
            } catch (NumberFormatException e) {
                initialValueFloat = (start + end) / 2;
            }
        }
        float step = button.getStep();
        if (initialValueFloat>end || initialValueFloat<start) {
            if (Config.enableInformationMessage){
                Logger.info("The initial value of the number range input is out of bounds. Start: " + start + ", End: " + end + ", Initial Value: " + initialValueFloat + ". Setting to middle value.");
            }
            initialValueFloat = (start + end) / 2;
        }

        return DialogInput.numberRange(key, width, label, labelFormat, start, end, initialValueFloat, step);
    }
}
