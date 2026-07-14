package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface DialogBuilderInput {
    DialogInputType getBodyType();
    default DialogInput build(@NotNull Player player, @NotNull InputButton button) {
        return this.build(player, button, new Placeholders());
    }
    DialogInput build(Player player, InputButton button, @NotNull Placeholders placeholders);
}
