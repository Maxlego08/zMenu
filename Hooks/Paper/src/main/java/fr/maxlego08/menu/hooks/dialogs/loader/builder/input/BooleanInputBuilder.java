package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderInput;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BooleanInputBuilder implements DialogBuilderInput {
    private final ZDialogManager dialogManager;
    private final MenuPlugin menuPlugin;

    public BooleanInputBuilder(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.dialogManager = dialogManager;
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.BOOLEAN;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();
        Component label = this.dialogManager.getPaperComponent().getComponent(this.menuPlugin.parse(player, button.getLabel()));
        Optional<Boolean> initialValueSupplier = button.getInitialValueSupplier();
        boolean initialValue;
        initialValue = initialValueSupplier.orElseGet(() -> Boolean.parseBoolean(this.menuPlugin.parse(player, button.getInitialValueBool())));

        String onTrueText = this.menuPlugin.parse(player, button.getTextTrue());
        String onFalseText = this.menuPlugin.parse(player, button.getTextFalse());

        return DialogInput.bool(key,label,initialValue,onTrueText,onFalseText);
    }
}
