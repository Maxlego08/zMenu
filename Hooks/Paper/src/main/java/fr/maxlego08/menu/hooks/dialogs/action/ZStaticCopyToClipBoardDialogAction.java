package fr.maxlego08.menu.hooks.dialogs.action;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.DialogButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZStaticCopyToClipBoardDialogAction implements ZDialogAction {
    private final @NotNull String text;

    public ZStaticCopyToClipBoardDialogAction(@NotNull String text) {
        this.text = text;
    }

    @Override
    public DialogAction build(@NotNull List<@NotNull DialogInput> inputs, @NotNull Player player, @NotNull MenuPlugin plugin, @NotNull InventoryEngine inventoryEngine, @Nullable DialogButton<?> button, @NotNull Placeholders placeholders) {
        return DialogAction.staticAction(ClickEvent.copyToClipboard(plugin.parse(player, placeholders.parse(this.text))));
    }
}
