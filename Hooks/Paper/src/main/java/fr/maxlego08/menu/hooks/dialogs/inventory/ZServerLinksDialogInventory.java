package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.ServerLinksDialogInventory;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.ComponentMeta;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZServerLinksDialogInventory extends AbstractButtonUtilsInventory implements ServerLinksDialogInventory {
    private ActionButtonRecord actionButton;
    private final int numberOfColumns;
    private final int buttonWidth = 100;

    public ZServerLinksDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, @NotNull ActionButtonRecord actionButton, int numberOfColumns) {
        super(plugin, name, fileName, externalTitle);
        this.dialogType = DialogType.SERVER_LINKS;
        this.actionButton = actionButton;
        this.numberOfColumns = numberOfColumns;
    }

    @Override
    public void setExitActionButton(ActionButtonRecord actionButtonRecord) {
        this.actionButton = actionButtonRecord;
    }

    @Override
    public ActionButtonRecord getExitActionButton(@NotNull Player player) {
        return this.actionButton != null ? this.actionButton.parse(player) : null;
    }

    @Override
    public ActionButtonRecord getExitActionButton() {
        return this.actionButton;
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull ComponentMeta paperComponent) {
        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player, paperComponent);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player, paperComponent);

        return Dialog.create(builder -> {
            builder.empty()
                    .type(io.papermc.paper.registry.data.dialog.type.DialogType.serverLinks(
                            this.createActionButton(this.actionButton.parse(player), dialogInputsForPlayer, paperComponent),
                            this.numberOfColumns,
                            this.buttonWidth
                    ))
                    .base(
                            this.createDialogBase(paperComponent, player, dialogBodiesForPlayer, dialogInputsForPlayer)
                    );
        });
    }
}
