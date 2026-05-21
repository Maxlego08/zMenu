package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;

public interface DialogBuilderInput {
    DialogInputType getBodyType();
    DialogInput build(Player player, InputButton button);
}
