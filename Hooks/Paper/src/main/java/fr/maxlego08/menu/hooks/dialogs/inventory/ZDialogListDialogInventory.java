package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.inventory.dialog.DialogListDialogInventory;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.record.dialogs.DialogListBackButton;
import fr.maxlego08.menu.api.utils.record.dialogs.DialogListEntry;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZDialogListDialogInventory extends AbstractButtonUtilsInventory implements DialogListDialogInventory {
    private final List<DialogListEntry> entries;
    private int numberOfColumns;
    private int buttonWidth;
    private @Nullable ActionButtonRecord exitButton;
    private @NotNull DialogListBackButton backButton;

    public ZDialogListDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, @NotNull List<DialogListEntry> entries, int numberOfColumns, int buttonWidth, @Nullable ActionButtonRecord exitButton, @NotNull DialogListBackButton backButton) {
        super(plugin, name, fileName, externalTitle, DialogType.DIALOG_LIST);
        this.entries = entries;
        this.numberOfColumns = numberOfColumns;
        this.buttonWidth = buttonWidth;
        this.exitButton = exitButton;
        this.backButton = backButton;
    }

    @Override
    public @NotNull List<DialogListEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void addEntry(@NotNull DialogListEntry entry) {
        this.entries.add(entry);
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
    public int getButtonWidth() {
        return this.buttonWidth;
    }

    @Override
    public void setButtonWidth(int buttonWidth) {
        this.buttonWidth = buttonWidth;
    }

    @Override
    public @NotNull DialogListBackButton getBackButton() {
        return this.backButton;
    }

    @Override
    public void setBackButton(@NotNull DialogListBackButton backButton) {
        this.backButton = backButton;
    }

    @Override
    public void setExitActionButton(@Nullable ActionButtonRecord actionButtonRecord) {
        this.exitButton = actionButtonRecord;
    }

    @Override
    public @Nullable ActionButtonRecord getExitActionButton(@NotNull Player player) {
        return this.exitButton != null ? this.exitButton.parse(player) : null;
    }

    @Override
    public @Nullable ActionButtonRecord getExitActionButton() {
        return this.exitButton;
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        List<DialogBody> dialogBodiesForPlayer = this.getDialogBodiesForPlayer(player, paperComponent);
        List<DialogInput> dialogInputsForPlayer = this.getDialogInputsForPlayer(player, paperComponent);

        // Built before entering this list's frame, so a nested list receives the back button of its parent
        ActionButton exitAction = this.exitOrBackButton(this.exitButton != null ? this.createActionButton(this.exitButton.parse(player), dialogInputsForPlayer, paperComponent, placeholders, player, inventoryEngine, null) : null);
        ActionButton backAction = this.backButton.enabled() ? this.createBackButton(player, paperComponent) : null;

        List<Dialog> dialogs = DialogListBuildContext.within(this, backAction, () -> this.buildEntries(player, paperComponent, inventoryEngine, placeholders));

        return Dialog.create(builder -> builder.empty()
                .type(io.papermc.paper.registry.data.dialog.type.DialogType.dialogList(
                        RegistrySet.valueSet(RegistryKey.DIALOG, dialogs),
                        exitAction,
                        this.numberOfColumns,
                        this.buttonWidth
                ))
                .base(this.createDialogBase(paperComponent, player, dialogBodiesForPlayer, dialogInputsForPlayer)));
    }

    private List<Dialog> buildEntries(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        List<Dialog> dialogs = new ArrayList<>(this.entries.size());
        for (DialogListEntry entry : this.entries) {
            Dialog dialog = switch (entry.source()) {
                case ZMENU -> this.buildZMenuEntry(entry.reference(), player, paperComponent, inventoryEngine, placeholders);
                case REGISTRY -> this.getRegistryEntry(entry.reference());
            };
            if (dialog != null) {
                dialogs.add(dialog);
            }
        }
        return dialogs;
    }

    private @Nullable Dialog buildZMenuEntry(@NotNull String reference, @NotNull Player player, @NotNull PaperMetaUpdater paperComponent, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        Optional<DialogInventory> optional = this.menuPlugin.getDialogManager().getDialog(reference);
        if (optional.isEmpty()) {
            this.debug("the dialog " + reference + " does not exist");
            return null;
        }

        DialogInventory dialogInventory = optional.get();
        if (DialogListBuildContext.isBuilding(dialogInventory)) {
            this.debug("the dialog " + reference + " is already open in this list, which would loop");
            return null;
        }
        if (dialogInventory.getDialogType() == DialogType.DIALOG_LIST && DialogListBuildContext.isTooDeep()) {
            this.debug("the dialog " + reference + " nests more than " + Configuration.dialogListMaxDepth + " dialog lists (dialog-list-max-depth in config.yml)");
            return null;
        }

        if (!this.meetsRequirement(dialogInventory.getOpenRequirement(), player, inventoryEngine, placeholders)) {
            return null;
        }

        try {
            return dialogInventory.buildDialog(player, paperComponent, inventoryEngine, placeholders.child());
        } catch (Exception exception) {
            this.debug("the dialog " + reference + " could not be built: " + exception.getMessage());
            return null;
        }
    }

    /**
     * Returns an inline copy of a registry dialog. The client cannot decode a list mixing registry
     * references with inline dialogs, and zMenu dialogs are always inline.
     */
    private @Nullable Dialog getRegistryEntry(@NotNull String reference) {
        NamespacedKey key = NamespacedKey.fromString(reference);
        if (key == null || RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG).get(key) == null) {
            this.debug("the registry dialog " + reference + " does not exist");
            return null;
        }
        return Dialog.create(builder -> builder.copyFrom(TypedKey.create(RegistryKey.DIALOG, key)));
    }

    /**
     * Checks the requirement without running its success or deny actions, so hidden entries stay silent.
     */
    private boolean meetsRequirement(@Nullable Requirement requirement, @NotNull Player player, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        if (requirement == null) {
            return true;
        }
        int success = 0;
        for (Permissible permissible : requirement.getRequirements()) {
            if (permissible.hasPermission(player, null, inventoryEngine, placeholders)) {
                success++;
            }
        }
        return success >= requirement.getMinimumRequirements();
    }

    private ActionButton createBackButton(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent) {
        DialogAction action = DialogAction.customClick((view, audience) -> {
            if (audience instanceof Player viewer) {
                this.menuPlugin.getScheduler().runAtEntity(viewer, task -> this.menuPlugin.getDialogManager().openDialog(viewer, this));
            }
        }, ClickCallback.Options.builder().uses(ClickCallback.UNLIMITED_USES).build());
        String tooltip = this.backButton.tooltip();
        return ActionButton.create(
                paperComponent.getComponent(this.menuPlugin.parse(player, this.backButton.label())),
                tooltip.isEmpty() ? null : paperComponent.getComponent(this.menuPlugin.parse(player, tooltip)),
                this.backButton.width(),
                action
        );
    }

    private void debug(String message) {
        if (Configuration.enableDebug) {
            Logger.info("Dialog list " + this.getFileName() + ": " + message + ", the entry is skipped.", Logger.LogType.WARNING);
        }
    }
}
