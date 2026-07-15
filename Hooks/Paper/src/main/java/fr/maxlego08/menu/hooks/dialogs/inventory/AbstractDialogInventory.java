package fr.maxlego08.menu.hooks.dialogs.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.DialogButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.api.utils.InventoryReplacement;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.record.dialogs.ZDialogInventoryBuild;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.*;

public abstract class AbstractDialogInventory implements DialogInventory {

    protected final MenuPlugin menuPlugin;
    private final String fileName;
    private File file;
    private InventoryReplacement inventoryReplacement;

    private final String name;
    private final String externalTitle;
    private boolean canCloseWithEscape = true;
    private boolean pause = false;
    private String afterAction = "CLOSE";
    protected final DialogType dialogType;
    private List<BodyButton> bodyButtons = new ArrayList<>();
    private List<InputButton> inputButtons = new ArrayList<>();

    private final List<ConditionalName> conditionalNames = new ArrayList<>();
    private String targetPlayerNamePlaceholder;
    private Requirement openRequirement;

    public AbstractDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle, DialogType dialogType) {
        this.menuPlugin = plugin;
        this.name = name;
        this.fileName = fileName.endsWith(".yml") ? fileName.replace(".yml", "") : fileName;
        this.externalTitle = externalTitle;
        this.dialogType = dialogType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getName(@NotNull Player player, InventoryEngine inventoryDefault, Placeholders placeholders) {
        if (!this.conditionalNames.isEmpty()) {
            Optional<ConditionalName> optional = this.conditionalNames.stream().filter(conditionalName -> conditionalName.hasPermission(player, null, inventoryDefault, placeholders)).max(Comparator.comparingInt(ConditionalName::priority));

            if (optional.isPresent()) {
                ConditionalName conditionalName = optional.get();
                return conditionalName.name();
            }
        }
        return this.menuPlugin.parse(player, this.name);
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public MenuPlugin getPlugin() {
        return this.menuPlugin;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean isPause() {
        return this.pause;
    }

    @Override
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public boolean canCloseWithEscape() {
        return this.canCloseWithEscape;
    }

    @Override
    public void setCanCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
    }
    @Override
    public @NonNull String getExternalTitle() {
        return this.externalTitle;
    }

    @Override
    public DialogType getDialogType() {
        return this.dialogType;
    }

    @Override
    public List<BodyButton> getBodyButtons() {
        return this.bodyButtons;
    }

    @Override
    public List<InputButton> getInputButtons() {
        return this.inputButtons;
    }


    @Override
    public void setBodyButtons(List<BodyButton> bodyButtons) {
        this.bodyButtons = bodyButtons != null ? bodyButtons : new ArrayList<>();
    }

    @Override
    public void setInputButtons(List<InputButton> inputButtons) {
        this.inputButtons = inputButtons != null ? inputButtons : new ArrayList<>();
    }

    @Override
    public String getAfterAction() {
        return this.afterAction;
    }

    @Override
    public void setAfterAction(String afterAction) {
        this.afterAction = afterAction;
    }

    @Override
    public ZDialogInventoryBuild getBuild(Player player) {
        return new ZDialogInventoryBuild(
                this.menuPlugin.parse(player, this.name),
                this.menuPlugin.parse(player, this.externalTitle), this.canCloseWithEscape
        );
    }

    @Deprecated
    @Override
    public void setExitActionButton(ActionButtonRecord actionButtonRecord) {
    }

    @Deprecated
    @Override
    public ActionButtonRecord getExitActionButton(@NotNull Player player) {
        return null;
    }

    @Deprecated
    @Override
    public ActionButtonRecord getExitActionButton() {
        return null;
    }

    public void setOpenRequirement(Requirement openRequirement) {
        this.openRequirement = openRequirement;
    }

    @Override
    public Requirement getOpenRequirement() {
        return this.openRequirement;
    }

    @Override
    public List<ConditionalName> getConditionalNames() {
        return this.conditionalNames;
    }

    @Override
    public String getTargetPlayerNamePlaceholder() {
        return this.targetPlayerNamePlaceholder;
    }

    @Override
    public void setTitleAnimation(TitleAnimation load) {
        // NotSupported
    }

    //Not supported by dialogs
    @Override
    public TitleAnimation getTitleAnimation() {
        return null;
    }

    //TODO
    @Override
    public List<Action> getOpenActions() {
        return List.of();
    }

    //TODO
    @Override
    public List<Action> getCloseActions() {
        return List.of();
    }

    @Override
    public ClearInvType getClearInvType() {
        return null;
    }

    @Override
    public boolean isClickLimiterEnabled() {
        return false;
    }

    @Override
    public @Nullable InventoryReplacement getInventoryReplacement() {
        return this.inventoryReplacement;
    }

    @Override
    public void setInventoryReplacement(InventoryReplacement inventoryReplacement) {
        this.inventoryReplacement = inventoryReplacement;
    }

    public void setTargetPlayerNamePlaceholder(String targetPlaceholder) {
        this.targetPlayerNamePlaceholder = targetPlaceholder;
    }

    @Override
    public List<BodyButton> getDialogBodies() {
        return this.bodyButtons;
    }

    @Override
    public List<BodyButton> getDialogBodies(Player player) {
        return this.filterByViewRequirement(this.bodyButtons, player);
    }

    @Override
    public List<InputButton> getDialogInputs() {
        return this.inputButtons;
    }
    @Override
    public List<InputButton> getDialogInputs(Player player) {
        return this.filterByViewRequirement(this.inputButtons, player);
    }

    @SuppressWarnings("unchecked")
    protected <T extends DialogButton<?>> List<T> filterByViewRequirement(List<T> buttons, Player player) {
        List<T> visibleButtons = new ArrayList<>();
        for (T button : buttons) {
            Button masterParent = button.getMasterParentButton();
            if (button.getClass().isInstance(masterParent)) {
                T visible = this.getFirstVisibleButtonRecursive((T) masterParent, player);
                if (visible != null) {
                    visibleButtons.add(visible);
                }
            }
        }
        return visibleButtons;
    }

    @SuppressWarnings("unchecked")
    private <T extends DialogButton<?>> T getFirstVisibleButtonRecursive(T button, Player player) {
        if (button.hasPermission()) {
            boolean hasPermission = button.checkPermission(player, this.menuPlugin.getInventoryManager().getFakeInventory(), new Placeholders());
            if (!hasPermission) {
                if (button.hasElseButton()) {
                    return this.getFirstVisibleButtonRecursive((T) button.getElseButton(), player);
                } else {
                    return null;
                }
            } else {
                return button;
            }
        } else {
            return button;
        }
    }

    protected DialogBase createDialogBase(@NotNull PaperMetaUpdater paperComponent, @NotNull Player player, @NotNull List<DialogBody> dialogBodies, @NotNull List<DialogInput> dialogInputs) {
        DialogBase.Builder builder = DialogBase.builder(paperComponent.getComponent(this.menuPlugin.parse(player, this.name)));
        builder.externalTitle(paperComponent.getComponent(this.menuPlugin.parse(player, this.externalTitle)));
        builder.canCloseWithEscape(this.canCloseWithEscape);
        builder.pause(this.pause);
        try {
            builder.afterAction(DialogBase.DialogAfterAction.valueOf(this.afterAction.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            builder.afterAction(DialogBase.DialogAfterAction.CLOSE);
        }
        builder.body(dialogBodies);
        builder.inputs(dialogInputs);
        return builder.build();
    }

    protected @NotNull List<DialogBody> getDialogBodiesForPlayer(@NotNull Player player, @NotNull PaperMetaUpdater componentMeta) {
        return this.buildDialogs(
                player,
                this.getDialogBodies(player),
                componentMeta
        );
    }

    protected @NotNull List<DialogInput> getDialogInputsForPlayer(@NotNull Player player, @NotNull PaperMetaUpdater componentMeta) {
        return this.buildDialogs(
                player,
                this.getDialogInputs(player),
                componentMeta
        );
    }


    protected <B extends DialogButton<T>, T> List<T> buildDialogs(
            Player player,
            List<B> buttons,
            @NotNull PaperMetaUpdater paperMetaUpdater
            ) {
        List<T> results = new ArrayList<>(buttons.size());
        DialogRenderContext<T, DialogInventory, PaperMetaUpdater, MenuPlugin> context = new DialogRenderContext<>(results, player, this, paperMetaUpdater, new Placeholders(), this.menuPlugin);
        for (B button : buttons) {
            if (button.hasCustomRender()) {
                button.onRender(context);
            } else {
                T build = button.build(context);
                if (build != null) {
                    results.add(build);
                }
            }
        }
        return results;
    }
}
