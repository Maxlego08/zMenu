package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface DialogBuilderBody {
    DialogBodyType getBodyType();

    default DialogBody build(Player player, BodyButton button) {
        return this.build(player, button, new Placeholders());
    }

    DialogBody build(Player player, BodyButton button, @NotNull Placeholders placeholders);
}
