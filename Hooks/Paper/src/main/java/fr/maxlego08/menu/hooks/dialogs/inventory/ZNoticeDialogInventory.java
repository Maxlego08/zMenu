package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.NoticeDialogInventory;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.robie.minimessageflow.Placeholder;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZNoticeDialogInventory extends AbstractDialogInventory implements NoticeDialogInventory {
    
    private ActionButtonRecord actionButtonRecord;

    public ZNoticeDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, @NotNull ActionButtonRecord actionButtonRecord) {
        super(plugin, name, fileName, externalTitle, DialogType.NOTICE);
        this.actionButtonRecord = actionButtonRecord;
    }

    @Override
    public String getLabel() {
        return this.actionButtonRecord.labelTemplate().source();
    }

    @Override
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    public String getLabel(Player player) {
        return TextService.get().flow().formatText(this.actionButtonRecord.labelTemplate().source(), Placeholder.empty(), player);
    }

    @Override
    public void setLabel(String label) {
        this.actionButtonRecord = this.actionButtonRecord.withLabel(label);
    }

    @Override
    public String getLabelTooltip() {
        return this.actionButtonRecord.tooltipTemplate().source();
    }

    @Override
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    public String getLabelTooltip(Player player) {
        return TextService.get().flow().formatText(this.actionButtonRecord.tooltipTemplate().source(), Placeholder.empty(), player);
    }

    @Override
    public void setLabelTooltip(String labelTooltip) {
        this.actionButtonRecord = this.actionButtonRecord.withTooltip(labelTooltip);
    }

    @Override
    public int getLabelWidth() {
        return this.actionButtonRecord.width();
    }

    @Override
    public void setLabelWidth(int labelWidth) {
        this.actionButtonRecord = this.actionButtonRecord.withWidth(labelWidth);
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player, paperComponent, placeholders);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player, paperComponent, placeholders);
        ActionButtonRecord record = this.actionButtonRecord;
        return Dialog.create(builder ->
                builder.empty()
                        .type(io.papermc.paper.registry.data.dialog.type.DialogType.notice(
                                ActionButton.create(
                                        record.labelTemplate().render(placeholders.values(), player),
                                        record.tooltipTemplate().render(placeholders.values(), player),
                                        record.width(),
                                        record.action().build(dialogInputsForPlayer, player, this.menuPlugin, inventoryEngine, null, placeholders)
                                )
                        )
                        ).base(
                                this.createDialogBase(player, dialogBodiesForPlayer, dialogInputsForPlayer, placeholders)
                        )
        );
    }
}
