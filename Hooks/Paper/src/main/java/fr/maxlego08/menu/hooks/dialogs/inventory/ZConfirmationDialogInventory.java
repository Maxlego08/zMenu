package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.ConfirmationDialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZConfirmationDialogInventory extends AbstractDialogInventory implements ConfirmationDialogInventory {
    private ActionButtonRecord yesActionButtonRecord;
    private ActionButtonRecord noActionButtonRecord;

    public ZConfirmationDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, ActionButtonRecord yesActionButtonRecord, ActionButtonRecord noActionButtonRecord) {
        super(plugin, name, fileName, externalTitle, DialogType.CONFIRMATION);
        this.yesActionButtonRecord = yesActionButtonRecord;
        this.noActionButtonRecord = noActionButtonRecord;
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
    public void setYesText(String yesText) {
        this.yesActionButtonRecord = new ActionButtonRecord(yesText, this.yesActionButtonRecord.tooltip(), this.yesActionButtonRecord.width(), this.yesActionButtonRecord.action());
    }

    @Override
    public void setNoText(String noText) {
        this.noActionButtonRecord = new ActionButtonRecord(noText, this.noActionButtonRecord.tooltip(), this.noActionButtonRecord.width(), this.noActionButtonRecord.action());
    }

    @Override
    public void setYesTooltip(String yesTooltip) {
        this.yesActionButtonRecord = new ActionButtonRecord(this.yesActionButtonRecord.label(), yesTooltip, this.yesActionButtonRecord.width(), this.yesActionButtonRecord.action());
    }

    @Override
    public void setNoTooltip(String noTooltip) {
        this.noActionButtonRecord = new ActionButtonRecord(this.noActionButtonRecord.label(), noTooltip, this.noActionButtonRecord.width(), this.noActionButtonRecord.action());
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
        this.yesActionButtonRecord = new ActionButtonRecord(this.yesActionButtonRecord.label(), this.yesActionButtonRecord.tooltip(), yesWidth, this.yesActionButtonRecord.action());
    }

    @Override
    public void setNoWidth(int noWidth) {
        this.noActionButtonRecord = new ActionButtonRecord(this.noActionButtonRecord.label(), this.noActionButtonRecord.tooltip(), noWidth, this.noActionButtonRecord.action());
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {

        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player, paperComponent);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player, paperComponent);

        return Dialog.create(builder ->
                builder.empty()
                        .type(io.papermc.paper.registry.data.dialog.type.DialogType.confirmation(
                                ActionButton.create(
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.yesActionButtonRecord.label())),
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.yesActionButtonRecord.tooltip())),
                                        this.yesActionButtonRecord.width(),
                                        this.yesActionButtonRecord.action().build(dialogInputsForPlayer, player, this.menuPlugin, inventoryEngine, null, placeholders)
                                ),
                                ActionButton.create(
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.noActionButtonRecord.label())),
                                        paperComponent.getComponent(this.menuPlugin.parse(player, this.noActionButtonRecord.tooltip())),
                                        this.noActionButtonRecord.width(),
                                        this.noActionButtonRecord.action().build(dialogInputsForPlayer, player, this.menuPlugin, inventoryEngine, null, placeholders)
                                )
                        ))
                        .base(
                                this.createDialogBase(paperComponent, player, dialogBodiesForPlayer, dialogInputsForPlayer)
                        )
                );
    }
}
