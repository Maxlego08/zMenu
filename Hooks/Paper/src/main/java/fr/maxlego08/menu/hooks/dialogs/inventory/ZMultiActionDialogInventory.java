package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.MultiActionDialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ZMultiActionDialogInventory extends AbstractButtonUtilsInventory implements MultiActionDialogInventory {
    private final List<ActionButtonRecord> actionButtons;
    private int numberOfColumns;
    private final @Nullable ActionButtonRecord exitButton;

    public ZMultiActionDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, @NotNull List<ActionButtonRecord> actionButtons, int numberOfColumns, ActionButtonRecord exitButton) {
        super(plugin, name, fileName, externalTitle);
        this.dialogType = DialogType.MULTI_ACTION;
        this.actionButtons = actionButtons;
        this.numberOfColumns = numberOfColumns;
        this.exitButton = exitButton;
    }

    @Override
    public List<ActionButtonRecord> getActionButtons(Player player) {
        List<ActionButtonRecord> actionButtonsParse = new ArrayList<>();
        for (ActionButtonRecord actionButtonRecord : this.actionButtons) {
            actionButtonsParse.add(actionButtonRecord.parse(player));
        }
        return actionButtonsParse;
    }

    @Override
    public List<ActionButtonRecord> getActionButtons() {
        return this.actionButtons;
    }

    @Override
    public void addActionButton(ActionButtonRecord actionButton) {
        this.actionButtons.add(actionButton);
    }

    @Override
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    @Override
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player, paperComponent);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player, paperComponent);

        return Dialog.create(builder -> {
            builder.empty()
                    .type(io.papermc.paper.registry.data.dialog.type.DialogType.multiAction(
                            this.createActionButtons(dialogInputsForPlayer, this.getActionButtons(player), paperComponent, placeholders, player, inventoryEngine, null),
                            this.exitButton != null ? this.createActionButton(this.exitButton.parse(player), dialogInputsForPlayer, paperComponent, placeholders, player, inventoryEngine, null) : null,
                            this.numberOfColumns
                    ))
                    .base(
                            this.createDialogBase(paperComponent, player, dialogBodiesForPlayer, dialogInputsForPlayer)
                    );
        });
    }
}
