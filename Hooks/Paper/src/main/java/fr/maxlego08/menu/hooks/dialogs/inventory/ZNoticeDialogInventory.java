package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.NoticeDialogInventory;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.ComponentMeta;
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
        super(plugin, name, fileName, externalTitle);
        this.dialogType = DialogType.NOTICE;
        this.actionButtonRecord = actionButtonRecord;
    }

    @Override
    public List<Requirement> getActions() {
        return this.actionButtonRecord.actions();
    }

    @Override
    public String getLabel() {
        return this.actionButtonRecord.label();
    }

    @Override
    public String getLabel(Player player) {
        return this.menuPlugin.parse(player, this.actionButtonRecord.label());
    }

    @Override
    public void setLabel(String label) {
        this.actionButtonRecord = new ActionButtonRecord(label, this.actionButtonRecord.tooltip(), this.actionButtonRecord.width(), this.actionButtonRecord.actions(), this.actionButtonRecord.usageLimit(), this.actionButtonRecord.actionDurationLimit());
    }

    @Override
    public String getLabelTooltip() {
        return this.actionButtonRecord.tooltip();
    }

    @Override
    public String getLabelTooltip(Player player) {
        return this.menuPlugin.parse(player, this.actionButtonRecord.tooltip());
    }

    @Override
    public void setLabelTooltip(String labelTooltip) {
        this.actionButtonRecord = new ActionButtonRecord(this.actionButtonRecord.label(), labelTooltip, this.actionButtonRecord.width(), this.actionButtonRecord.actions(), this.actionButtonRecord.usageLimit(), this.actionButtonRecord.actionDurationLimit());
    }

    @Override
    public int getLabelWidth() {
        return this.actionButtonRecord.width();
    }

    @Override
    public void setLabelWidth(int labelWidth) {
        this.actionButtonRecord = new ActionButtonRecord(this.actionButtonRecord.label(), this.actionButtonRecord.tooltip(), labelWidth, this.actionButtonRecord.actions(), this.actionButtonRecord.usageLimit(), this.actionButtonRecord.actionDurationLimit());
    }

    @Override
    public void addAction(List<Requirement> action) {
        this.actionButtonRecord.actions().addAll(action);
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull ComponentMeta paperComponent) {
        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player);
        ActionButtonRecord parsedRecord = this.actionButtonRecord.parse(player);
        return Dialog.create(builder ->
                builder.empty()
                        .type(io.papermc.paper.registry.data.dialog.type.DialogType.notice(
                                ActionButton.create(
                                        paperComponent.getComponent(parsedRecord.label()),
                                        paperComponent.getComponent(parsedRecord.tooltip()),
                                        parsedRecord.width(),
                                        this.createAction(dialogInputsForPlayer, parsedRecord.actions(), parsedRecord.usageLimit(), parsedRecord.actionDurationLimit())
                                )
                        )
                        ).base(
                                this.createDialogBase(paperComponent, player, dialogBodiesForPlayer, dialogInputsForPlayer)
                        )
        );
    }
}
