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

public class ZStaticSuggestCommandDialogAction implements ZDialogAction {
    private final @NotNull String command;

    public ZStaticSuggestCommandDialogAction(@NotNull String command) {
        this.command = command;
    }

    @Override
    public DialogAction build(@NotNull List<@NotNull DialogInput> inputs, @NotNull Player player, @NotNull MenuPlugin plugin, @NotNull InventoryEngine inventoryEngine, @Nullable DialogButton<?> button, @NotNull Placeholders placeholders) {
        return DialogAction.staticAction(ClickEvent.suggestCommand(plugin.parse(player, placeholders.parse(this.command))));
    }
}
