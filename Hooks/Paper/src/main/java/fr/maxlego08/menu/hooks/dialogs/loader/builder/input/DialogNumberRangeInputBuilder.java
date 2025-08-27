package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.utils.BuilderHelper;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DialogNumberRangeInputBuilder extends BuilderHelper implements DialogInputBuilderInt {
    private final ZDialogManager dialogManager;

    public DialogNumberRangeInputBuilder(ZDialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.NUMBER_RANGE;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();
        int width = button.getWidth();
        Component label = this.dialogManager.getPaperComponent().getComponent(papi(button.getLabel(),player));
        String labelFormat = papi(button.getLabelFormat(),player);
        float start = button.getStart();
        float end = button.getEnd();
        Optional<Float> initialValueSupplier = button.getInitialValueRangeSupplier();
        float initialValueFloat;
        if (initialValueSupplier.isPresent()) {
             initialValueFloat = initialValueSupplier.get();
        } else {
            String initialValue = papi(button.getInitialValueRange(),player);
            try {
                initialValueFloat = Float.parseFloat(initialValue);
            } catch (NumberFormatException e) {
                initialValueFloat = (start + end) / 2;
            }
        }
        float step = button.getStep();

        return DialogInput.numberRange(key, width, label, labelFormat, start, end, initialValueFloat, step);
    }
}
