package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class DialogNumberRangeInputBuilder implements DialogInputBuilderInt {

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.NUMBER_RANGE;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        PaperComponent paperComponent = PaperComponent.getInstance();

        String key = button.getKey();
        int width = button.getWidth();
        Component label = paperComponent.getComponent(PlaceholderAPI.setPlaceholders(player,button.getLabel()));
        String labelFormat = PlaceholderAPI.setPlaceholders(player, button.getLabelFormat());
        float start = button.getStart();
        float end = button.getEnd();
        String initialValue = PlaceholderAPI.setPlaceholders(player, button.getInitialValueRange());
        float initialValueFloat;
        try {
            initialValueFloat = Float.parseFloat(initialValue);
        } catch (NumberFormatException e) {
            initialValueFloat = (start + end) / 2;
        }
        float step = button.getStep();

        return DialogInput.numberRange(key, width, label, labelFormat, start, end, initialValueFloat, step);
    }
}
