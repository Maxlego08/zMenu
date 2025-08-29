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

public class DialogBooleanInputBuilder extends BuilderHelper implements DialogInputBuilderInt {
    private final ZDialogManager dialogManager;

    public DialogBooleanInputBuilder(ZDialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.BOOLEAN;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();
        Component label = this.dialogManager.getPaperComponent().getComponent(papi(button.getLabel(),player));
        Optional<Boolean> initialValueSupplier = button.getInitialValueSupplier();
        boolean initialValue;
        initialValue = initialValueSupplier.orElseGet(() -> Boolean.parseBoolean(papi(button.getInitialValueBool(), player)));

        String onTrueText = papi(button.getTextTrue(), player);
        String onFalseText = papi(button.getTextFalse(), player);

        return DialogInput.bool(key,label,initialValue,onTrueText,onFalseText);
    }
}
