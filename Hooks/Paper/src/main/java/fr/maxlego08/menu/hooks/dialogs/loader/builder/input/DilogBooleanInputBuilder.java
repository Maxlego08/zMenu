package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.hooks.dialogs.DialogManager;
import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class DilogBooleanInputBuilder implements DialogInputBuilderInt {
    private final DialogManager dialogManager;

    public DilogBooleanInputBuilder(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.BOOLEAN;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();
        Component label = this.dialogManager.getPaperComponent().getComponent(PlaceholderAPI.setPlaceholders(player, button.getLabel()));
        boolean initialValue = Boolean.parseBoolean(PlaceholderAPI.setPlaceholders(player, button.getInitialValueBool()));
        String onTrueText = PlaceholderAPI.setPlaceholders(player, button.getTextTrue());
        String onFalseText = PlaceholderAPI.setPlaceholders(player, button.getTextFalse());

        return DialogInput.bool(key,label,initialValue,onTrueText,onFalseText);
    }
}
