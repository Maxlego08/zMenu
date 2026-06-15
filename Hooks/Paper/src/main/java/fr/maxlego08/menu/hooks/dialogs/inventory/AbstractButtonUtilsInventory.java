package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.ComponentMeta;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractButtonUtilsInventory extends AbstractDialogInventory {

    protected AbstractButtonUtilsInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle) {
        super(plugin, name, fileName, externalTitle);
    }

    protected List<ActionButton> createActionButtons(List<DialogInput> inputs, List<ActionButtonRecord> actionButtonRecords, @NotNull ComponentMeta paperComponent) {
        List<ActionButton> actionButtons = new ArrayList<>();
        for (ActionButtonRecord actionButtonRecord : actionButtonRecords) {
            ActionButton actionButton = this.createActionButton(actionButtonRecord, inputs, paperComponent);
            if (actionButton != null) {
                actionButtons.add(actionButton);
            }
        }
        return actionButtons;
    }

    protected ActionButton createActionButton(ActionButtonRecord actionButtonRecord, List<DialogInput> inputs, @NotNull ComponentMeta paperComponent) {
        if (actionButtonRecord == null) {
            return null;
        }
        return ActionButton.create(paperComponent.getComponent(actionButtonRecord.label()), paperComponent.getComponent(actionButtonRecord.tooltip()), actionButtonRecord.width(), this.createAction(inputs,actionButtonRecord.actions(), actionButtonRecord.usageLimit(), actionButtonRecord.actionDurationLimit()));
    }
}
