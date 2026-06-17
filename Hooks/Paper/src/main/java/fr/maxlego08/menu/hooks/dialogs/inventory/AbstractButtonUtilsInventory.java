package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.DialogButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractButtonUtilsInventory extends AbstractDialogInventory {

    protected AbstractButtonUtilsInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle) {
        super(plugin, name, fileName, externalTitle);
    }

    protected List<ActionButton> createActionButtons(List<DialogInput> inputs, List<ActionButtonRecord> actionButtonRecords, @NotNull PaperMetaUpdater paperComponent, @NotNull Placeholders placeholders, @NotNull Player player, @NotNull InventoryEngine inventoryEngine, @Nullable DialogButton<?> button) {
        List<ActionButton> actionButtons = new ArrayList<>();
        for (ActionButtonRecord actionButtonRecord : actionButtonRecords) {
            ActionButton actionButton = this.createActionButton(actionButtonRecord, inputs, paperComponent, placeholders, player, inventoryEngine, button);
            if (actionButton != null) {
                actionButtons.add(actionButton);
            }
        }
        return actionButtons;
    }

    protected ActionButton createActionButton(ActionButtonRecord actionButtonRecord, List<DialogInput> inputs, @NotNull PaperMetaUpdater paperComponent, @NotNull Placeholders placeholders, @NotNull Player player, @NotNull InventoryEngine inventoryEngine, @Nullable DialogButton<?> button) {
        if (actionButtonRecord == null) {
            return null;
        }
        return ActionButton.create(paperComponent.getComponent(actionButtonRecord.label()), paperComponent.getComponent(actionButtonRecord.tooltip()), actionButtonRecord.width(), actionButtonRecord.action().build(inputs, player, this.menuPlugin, inventoryEngine, button, placeholders));
    }
}
