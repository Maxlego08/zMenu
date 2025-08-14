package fr.maxlego08.menu.hooks.dialogs.utils.loader;

import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogInputType;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;

public interface DialogInputBuilderInt {
    DialogInputType getBodyType();
    DialogInput build(Player player, InputButton button);
}
