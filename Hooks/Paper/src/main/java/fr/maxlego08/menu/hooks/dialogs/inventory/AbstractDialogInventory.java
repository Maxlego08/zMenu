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
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.dialogs.record.ZDialogInventoryBuild;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderClass;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.*;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

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
    protected DialogType dialogType = DialogType.NOTICE;
    private List<BodyButton> bodyButtons = new ArrayList<>();
    private List<InputButton> inputButtons = new ArrayList<>();

    private final List<ConditionalName> conditionalNames = new ArrayList<>();
    private String targetPlayerNamePlaceholder;
    private Requirement openRequirement;

    public AbstractDialogInventory(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull String fileName, @NotNull String externalTitle) {
        this.menuPlugin = plugin;
        this.name = name;
        this.fileName = fileName.endsWith(".yml") ? fileName.replace(".yml", "") : fileName;
        this.externalTitle = externalTitle;
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
    public void setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
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

    @Deprecated
    @Override
    public List<Requirement> getYesActions() {
        return Collections.emptyList();
    }

    @Deprecated
    @Override
    public List<Requirement> getNoActions() {
        return Collections.emptyList();
    }

    @Deprecated
    @Override
    public void addYesAction(List<Requirement> actions) {
    }

    @Deprecated
    @Override
    public void addNoAction(List<Requirement> actions) {
    }

    @Deprecated
    @Override
    public String getYesText() {
        return "";
    }

    @Deprecated
    @Override
    public String getYesText(Player player) {
        return "";
    }

    @Deprecated
    @Override
    public void setYesText(String yesText) {
    }

    @Deprecated
    @Override
    public String getNoText() {
        return "";
    }

    @Deprecated
    @Override
    public String getNoText(Player player) {
        return "";
    }

    @Deprecated
    @Override
    public void setNoText(String noText) {
    }

    @Deprecated
    @Override
    public String getYesTooltip() {
        return "";
    }

    @Deprecated
    @Override
    public String getYesTooltip(Player player) {
        return "";
    }

    @Deprecated
    @Override
    public void setYesTooltip(String yesTooltip) {
    }

    @Deprecated
    @Override
    public String getNoTooltip() {
        return "";
    }

    @Deprecated
    @Override
    public String getNoTooltip(Player player) {
        return "";
    }

    @Deprecated
    @Override
    public int getYesWidth() {
        return 0;
    }

    @Deprecated
    @Override
    public int getNoWidth() {
        return 0;
    }

    @Deprecated
    @Override
    public void setYesWidth(int yesWidth) {
    }

    @Deprecated
    @Override
    public void setNoWidth(int noWidth) {
    }

    @Deprecated
    @Override
    public String getLabel() {
        return "";
    }

    @Deprecated
    @Override
    public String getLabel(Player player) {
        return "";
    }

    @Deprecated
    @Override
    public void setLabel(String label) {
    }

    @Deprecated
    @Override
    public String getLabelTooltip() {
        return "";
    }

    @Deprecated
    @Override
    public String getLabelTooltip(Player player) {
        return "";
    }

    @Deprecated
    @Override
    public void setLabelTooltip(String labelTooltip) {
    }

    @Deprecated
    @Override
    public int getLabelWidth() {
        return 0;
    }

    @Deprecated
    @Override
    public void setLabelWidth(int labelWidth) {
    }

    @Deprecated
    @Override
    public List<ActionButtonRecord> getActionButtons(Player player) {
        return Collections.emptyList();
    }

    @Deprecated
    @Override
    public List<ActionButtonRecord> getActionButtons() {
        return Collections.emptyList();
    }

    @Deprecated
    @Override
    public void addActionButton(ActionButtonRecord actionButton) {
    }

    @Deprecated
    @Override
    public int getNumberOfColumns() {
        return 0;
    }

    @Deprecated
    @Override
    public void setNumberOfColumns(int numberOfColumns) {
    }

    @Deprecated
    @Override
    public void addAction(List<Requirement> actions) {
    }

    @Deprecated
    @Override
    public List<Requirement> getActions() {
        return Collections.emptyList();
    }

    @Deprecated
    @Override
    public void setNoTooltip(String noTooltip) {
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

    public abstract Dialog buildDialog(@NotNull Player player, @NotNull ComponentMeta paperComponent);

    protected DialogBase createDialogBase(@NotNull ComponentMeta paperComponent, @NotNull Player player, @NotNull List<DialogBody> dialogBodies, @NotNull List<DialogInput> dialogInputs) {
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

    protected @NotNull List<DialogBody> getDialogBodiesForPlayer(@NotNull Player player, @NotNull ComponentMeta componentMeta) {
        return this.buildDialogs(
                player,
                this.getDialogBodies(player),
                BodyButton::getBodyType,
                DialogBuilderClass::getDialogBuilder,
                (builder, button) -> builder.build(player, button),
                componentMeta
        );
    }

    protected @NotNull List<DialogInput> getDialogInputsForPlayer(@NotNull Player player, @NotNull ComponentMeta componentMeta) {
        return this.buildDialogs(
                player,
                this.getDialogInputs(player),
                InputButton::getInputType,
                DialogBuilderClass::getDialogInputBuilder,
                (builder, button) -> builder.build(player, button),
                componentMeta
        );
    }


    protected DialogAction createAction(@NotNull List<DialogInput> inputs,@NotNull List<Requirement> requirements, int usageLimit, @Nullable TemporalAmount actionDurationLimit) {
        ClickCallback.Options.Builder builder = ClickCallback.Options.builder();
        builder.uses(usageLimit);
        if (actionDurationLimit != null) {
            builder.lifetime(actionDurationLimit);
        }
        return DialogAction.customClick((view,audience)-> {
            Placeholders placeholders = new Placeholders();
            for (DialogInput input : inputs) {
                String key = input.key();
                String value = null;

                Object rawValue;

                switch (input) {
                    case NumberRangeDialogInput numberRangeDialogInput -> {
                        rawValue = view.getFloat(key);
                        value = String.valueOf(rawValue);
                    }
                    case TextDialogInput textDialogInput -> {
                        rawValue = view.getText(key);
                        value = (String) rawValue;
                    }
                    case BooleanDialogInput booleanDialogInput -> {
                        rawValue = view.getBoolean(key);
                        value = String.valueOf(rawValue);
                        placeholders.register(key+"_text", (Boolean) rawValue ? booleanDialogInput.onTrue() : booleanDialogInput.onFalse());
                    }
                    case SingleOptionDialogInput singleOptionDialogInput -> {
                        rawValue = view.getText(key);
                        value = (String) rawValue;
                    }
                    default -> {
                    }
                }
                if (value == null) {
                    continue;
                }

                placeholders.register(key, value);
            }

            for (Requirement requirement : requirements) {
                requirement.execute((Player) audience, null, this.menuPlugin.getInventoryManager().getFakeInventory(), placeholders);
            }

        }, builder.build());
    }

    protected <B extends DialogButton<T>, T, TYPE, BUILDER> List<T> buildDialogs(
            Player player,
            List<B> buttons,
            Function<B, TYPE> typeExtractor,
            Function<TYPE, Optional<BUILDER>> builderResolver,
            BiFunction<BUILDER, B, T> builderExecutor,
            @NotNull ComponentMeta paperMetaUpdater
            ) {
        List<T> results = new ArrayList<>(buttons.size());
        for (B button : buttons) {
            if (button.hasCustomRender()) {
                DialogRenderContext<T> context = new DialogRenderContext<>(results, player, this, paperMetaUpdater);
                button.onRender(context);
            } else {
                TYPE type = typeExtractor.apply(button);
                if (type == null) {
                    continue;
                }
                Optional<BUILDER> builderOptional = builderResolver.apply(type);
                if (builderOptional.isPresent()) {
                    T value = builderExecutor.apply(builderOptional.get(), button);
                    if (value != null) {
                        results.add(value);
                    }
                }
            }
        }
        return results;
    }
}
