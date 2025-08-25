package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.DialogInventory;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.DialogType;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.dialogs.record.ZDialogInventoryBuild;
import fr.maxlego08.menu.hooks.dialogs.utils.BuilderHelper;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ZDialogInventory extends BuilderHelper implements DialogInventory {

    private final MenuPlugin menuPlugin;
    private final String fileName;
    private File file;

    private final String name;
    private final String externalTitle;
    private boolean canCloseWithEscape = true;
    private boolean pause = false;
    private String afterAction = "CLOSE";
    private DialogType dialogType = DialogType.NOTICE;
    private List<BodyButton> bodyButtons = new ArrayList<>();
    private List<InputButton> inputButtons = new ArrayList<>();

    // Notice
    private final List<Requirement> actions = new ArrayList<>();
    private String label;
    private String labelTooltip;
    private int labelWidth = 200;

    // When {@link DialogType#CONFIRM} is used
    private final List<Requirement> yesActions = new ArrayList<>();
    private String yesText = "Yes";
    private String yesTooltip = null;
    private int yesWidth = 100;

    private final List<Requirement> noActions = new ArrayList<>();
    private String noText = "No";
    private String noTooltip = null;
    private int noWidth = 100;

    // MultiAction
    private final List<ActionButtonRecord> actionButtons = new ArrayList<>();
    // Use numberOfColums

    // Server link
    private ActionButtonRecord actionButtonRecordServerLink;
    private int numberOfColumns = 1;

    public ZDialogInventory(MenuPlugin plugin, String name, String fileName, String externalTitle) {
        this.menuPlugin = plugin;
        this.name = name;
        this.fileName = fileName.endsWith(".yml") ? fileName.replace(".yml", "") : fileName;
        this.externalTitle = externalTitle;
    }


    @Override
    public String getName(Player player) {
        return papi(name, player);
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public MenuPlugin getPlugin() {
        return menuPlugin;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean isPause() {
        return pause;
    }

    @Override
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public boolean canCloseWithEscape() {
        return canCloseWithEscape;
    }

    @Override
    public void setCanCloseWithEscape(boolean canCloseWithEscape) {
        this.canCloseWithEscape = canCloseWithEscape;
    }
    @Override
    public String getExternalTitle() {
        return externalTitle;
    }
    @Override
    public void setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
    }
    @Override
    public DialogType getDialogType() {
        return dialogType;
    }

    @Override
    public List<BodyButton> getBodyButtons() {
        return bodyButtons;
    }

    @Override
    public List<InputButton> getInputButtons() {
        return inputButtons;
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
        return afterAction;
    }

    @Override
    public void setAfterAction(String afterAction) {
        this.afterAction = afterAction;
    }

    @Override
    public ZDialogInventoryBuild getBuild(Player player) {
        return new ZDialogInventoryBuild(parsePlaceholders(player, name),parsePlaceholders(player,externalTitle), canCloseWithEscape);
    }

    @Override
    public void setActionButtonServerLink(ActionButtonRecord actionButtonRecord) {
        this.actionButtonRecordServerLink = actionButtonRecord;
    }

    @Override
    public ActionButtonRecord getActionButtonServerLink(Player player) {
        if (actionButtonRecordServerLink != null) {
            return actionButtonRecordServerLink.parse(player);
        }
        return null;
    }

    @Override
    public ActionButtonRecord getActionButtonServerLink() {
        return actionButtonRecordServerLink;
    }

    @Override
    public List<BodyButton> getDialogBodies() {
        return this.bodyButtons;
    }

    @Override
    public List<BodyButton> getDialogBodies(Player player) {
        return filterByViewRequirement(bodyButtons, player, BodyButton::getViewRequirement);
    }

    @Override
    public List<InputButton> getDialogInputs() {
        return this.inputButtons;
    }
    @Override
    public List<InputButton> getDialogInputs(Player player) {
        return filterByViewRequirement(inputButtons, player, InputButton::getViewRequirement);

    }

    public String parsePlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) return "";
        return papi(text, player);
    }

    @Override
    public List<Requirement> getYesActions() {
        return yesActions;
    }

    @Override
    public List<Requirement> getNoActions() {
        return noActions;
    }

    @Override
    public void addYesAction(List<Requirement> actions) {
        this.yesActions.addAll(actions);
    }
    @Override
    public void addNoAction(List<Requirement> actions) {
        this.noActions.addAll(actions);
    }
    @Override
    public String getYesText() {
        return yesText;
    }

    @Override
    public String getYesText(Player player) {
        return parsePlaceholders(player, yesText);
    }

    @Override
    public void setYesText(String yesText) {
        this.yesText = yesText;
    }
    @Override
    public String getNoText() {
        return noText;
    }

    @Override
    public String getNoText(Player player) {
        return parsePlaceholders(player, noText);
    }

    @Override
    public void setNoText(String noText) {
        this.noText = noText;
    }
    @Override
    public String getYesTooltip() {
        return yesTooltip;
    }

    @Override
    public String getYesTooltip(Player player) {
        return parsePlaceholders(player, yesTooltip);
    }

    @Override
    public void setYesTooltip(String yesTooltip) {
        this.yesTooltip = yesTooltip;
    }
    @Override
    public String getNoTooltip() {
        return noTooltip;
    }

    @Override
    public String getNoTooltip(Player player) {
        return parsePlaceholders(player, noTooltip);
    }

    @Override
    public int getYesWidth() {
        return yesWidth;
    }

    @Override
    public int getNoWidth() {
        return noWidth;
    }

    @Override
    public void setYesWidth(int yesWidth) {
        this.yesWidth = yesWidth;
    }

    @Override
    public void setNoWidth(int noWidth) {
        this.noWidth = noWidth;
    }

    @Override
    public String getLabel() {
        return label != null ? label : "";
    }

    @Override
    public String getLabel(Player player) {
        return parsePlaceholders(player, label);
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabelTooltip() {
        return labelTooltip != null ? labelTooltip : "";
    }

    @Override
    public String getLabelTooltip(Player player) {
        return parsePlaceholders(player, labelTooltip);
    }

    @Override
    public void setLabelTooltip(String labelTooltip) {
        this.labelTooltip = labelTooltip;
    }

    @Override
    public int getLabelWidth() {
        return labelWidth;
    }

    @Override
    public void setLabelWidth(int labelWidth) {
        this.labelWidth = labelWidth;
    }

    @Override
    public List<ActionButtonRecord> getActionButtons(Player player) {
        List<ActionButtonRecord> actionButtonsParse = new ArrayList<>();
        for (ActionButtonRecord actionButtonRecord : actionButtons) {
            actionButtonsParse.add(actionButtonRecord.parse(player));
        }
        return actionButtonsParse;
    }

    @Override
    public List<ActionButtonRecord> getActionButtons() {
        return actionButtons;
    }

    @Override
    public void addActionButton(ActionButtonRecord actionButton) {
        if (actionButton != null) {
            this.actionButtons.add(actionButton);
        }
    }

    @Override
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    @Override
    public void setNumberOfColumns(int numberOfColumns) {
        if (numberOfColumns > 0) {
            this.numberOfColumns = numberOfColumns;
        } else {
            throw new IllegalArgumentException("Number of columns must be greater than 0");
        }
    }

    @Override
    public void addAction(List<Requirement> actions) {
        this.actions.addAll(actions);
    }

    @Override
    public List<Requirement> getActions() {
        return actions;
    }

    @Override
    public void setNoTooltip(String noTooltip) {
        this.noTooltip = noTooltip;
    }

    private <T> List<T> filterByViewRequirement(List<T> buttons, Player player, Function<T, Requirement> getter) {
        InventoryEngine fakeInventory = menuPlugin.getInventoryManager().getFakeInventory();
        Placeholders placeholder = new Placeholders();
        return buttons.stream()
                .filter(button -> {
                    Requirement requirement = getter.apply(button);
                    if (requirement == null) return true;
                    boolean canView = requirement.execute(player, null, fakeInventory, placeholder);
                    List<Action> actions = canView ? requirement.getSuccessActions() : requirement.getDenyActions();
                    for (Action action : actions) {
                        action.preExecute(player, null, fakeInventory, placeholder);
                    }
                    return canView;
                })
                .toList();
    }
}
