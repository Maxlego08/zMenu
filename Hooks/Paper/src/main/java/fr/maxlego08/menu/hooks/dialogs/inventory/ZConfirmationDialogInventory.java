package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.ConfirmationDialogInventory;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ZConfirmationDialogInventory extends AbstractDialogInventory implements ConfirmationDialogInventory {
    private ActionButtonRecord yesActionButtonRecord;
    private ActionButtonRecord noActionButtonRecord;

    public ZConfirmationDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, ActionButtonRecord yesActionButtonRecord, ActionButtonRecord noActionButtonRecord) {
        super(plugin, name, fileName, externalTitle);

        this.dialogType = DialogType.CONFIRMATION;

        this.yesActionButtonRecord = yesActionButtonRecord;
        this.noActionButtonRecord = noActionButtonRecord;
    }

    @Override
    public void addYesRequirements(@NonNull List<Requirement> requirements) {
        this.yesActionButtonRecord.actions().addAll(requirements);
    }

    @Override
    public void addYesRequirement(@NotNull Requirement requirement) {
        this.yesActionButtonRecord.actions().add(requirement);
    }

    @Override
    public void addNoRequirements(@NotNull List<Requirement> requirements) {
        this.noActionButtonRecord.actions().addAll(requirements);
    }

    @Override
    public void addNoRequirement(@NotNull Requirement requirement) {
        this.noActionButtonRecord.actions().add(requirement);
    }

    @Override
    public ActionButtonRecord getYesActionButtonRecord() {
        return this.yesActionButtonRecord;
    }

    @Override
    public ActionButtonRecord getNoActionButtonRecord() {
        return this.noActionButtonRecord;
    }

    @Override
    public List<Requirement> getYesRequirements() {
        return this.yesActionButtonRecord.actions();
    }

    @Override
    public List<Requirement> getNoRequirements() {
        return this.noActionButtonRecord.actions();
    }

    @Override
    public void setYesText(String yesText) {
        this.yesActionButtonRecord = new ActionButtonRecord(yesText, this.yesActionButtonRecord.tooltip(), this.yesActionButtonRecord.width(), this.yesActionButtonRecord.actions(), this.yesActionButtonRecord.usageLimit(), this.yesActionButtonRecord.actionDurationLimit());
    }

    @Override
    public void setNoText(String noText) {
        this.noActionButtonRecord = new ActionButtonRecord(noText, this.noActionButtonRecord.tooltip(), this.noActionButtonRecord.width(), this.noActionButtonRecord.actions(), this.noActionButtonRecord.usageLimit(), this.noActionButtonRecord.actionDurationLimit());
    }

    @Override
    public void setYesTooltip(String yesTooltip) {
        this.yesActionButtonRecord = new ActionButtonRecord(this.yesActionButtonRecord.label(), yesTooltip, this.yesActionButtonRecord.width(), this.yesActionButtonRecord.actions(), this.yesActionButtonRecord.usageLimit(), this.yesActionButtonRecord.actionDurationLimit());
    }

    @Override
    public void setNoTooltip(String noTooltip) {
        this.noActionButtonRecord = new ActionButtonRecord(this.noActionButtonRecord.label(), noTooltip, this.noActionButtonRecord.width(), this.noActionButtonRecord.actions(), this.noActionButtonRecord.usageLimit(), this.noActionButtonRecord.actionDurationLimit());
    }

    @Override
    public String getYesText() {
        return this.yesActionButtonRecord.label();
    }

    @Override
    public String getYesText(Player player) {
        return this.menuPlugin.parse(player, this.yesActionButtonRecord.label());
    }

    @Override
    public String getNoText() {
        return this.noActionButtonRecord.label();
    }

    @Override
    public String getNoText(Player player) {
        return this.menuPlugin.parse(player, this.noActionButtonRecord.label());
    }

    @Override
    public String getYesTooltip() {
        return this.yesActionButtonRecord.tooltip();
    }

    @Override
    public String getYesTooltip(Player player) {
        return this.menuPlugin.parse(player, this.yesActionButtonRecord.tooltip());
    }

    @Override
    public String getNoTooltip() {
        return this.noActionButtonRecord.tooltip();
    }

    @Override
    public String getNoTooltip(Player player) {
        return this.menuPlugin.parse(player, this.noActionButtonRecord.tooltip());
    }

    @Override
    public int getYesWidth() {
        return this.yesActionButtonRecord.width();
    }

    @Override
    public int getNoWidth() {
        return this.noActionButtonRecord.width();
    }

    @Override
    public void setYesWidth(int yesWidth) {
        this.yesActionButtonRecord = new ActionButtonRecord(this.yesActionButtonRecord.label(), this.yesActionButtonRecord.tooltip(), yesWidth, this.yesActionButtonRecord.actions(), this.yesActionButtonRecord.usageLimit(), this.yesActionButtonRecord.actionDurationLimit());
    }

    @Override
    public void setNoWidth(int noWidth) {
        this.noActionButtonRecord = new ActionButtonRecord(this.noActionButtonRecord.label(), this.noActionButtonRecord.tooltip(), noWidth, this.noActionButtonRecord.actions(), this.noActionButtonRecord.usageLimit(), this.noActionButtonRecord.actionDurationLimit());
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent) {

        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player, paperComponent);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player, paperComponent);

        return Dialog.create(builder ->
                builder.empty()
                        .type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(
                                ActionButton.create(
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.yesActionButtonRecord.label())),
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.yesActionButtonRecord.tooltip())),
                                        this.yesActionButtonRecord.width(),
                                        this.createAction(dialogInputsForPlayer, this.yesActionButtonRecord.actions(), this.yesActionButtonRecord.usageLimit(), this.yesActionButtonRecord.actionDurationLimit())
                                ),
                                ActionButton.create(
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.noActionButtonRecord.label())),
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.noActionButtonRecord.tooltip())),
                                        this.noActionButtonRecord.width(),
                                        this.createAction(dialogInputsForPlayer, this.noActionButtonRecord.actions(), this.noActionButtonRecord.usageLimit(), this.noActionButtonRecord.actionDurationLimit())
                                )
                        ))
                        .base(
                                this.createDialogBase(paperComponent, player, dialogBodiesForPlayer, dialogInputsForPlayer)
                        )
                );
    }
}
