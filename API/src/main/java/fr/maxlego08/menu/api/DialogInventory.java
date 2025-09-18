package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.DialogType;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.dialogs.record.ZDialogInventoryBuild;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public interface DialogInventory {
    String getName(Player player);

    String getExternalTitle();

    String getFileName();

    MenuPlugin getPlugin();

    File getFile();

    void setFile(File file);

    boolean canCloseWithEscape();

    void setCanCloseWithEscape(boolean canCloseWithEscape);

    String getAfterAction();

    void setAfterAction(String afterAction);

    boolean isPause();

    void setPause(boolean pause);

    void setDialogType(DialogType dialogType);

    DialogType getDialogType();

    List<BodyButton> getBodyButtons();

    List<InputButton> getInputButtons();

    void setBodyButtons(List<BodyButton> bodyButtons);

    void setInputButtons(List<InputButton> inputButtons);

    List<BodyButton> getDialogBodies();

    List<BodyButton> getDialogBodies(Player player);

    List<InputButton> getDialogInputs();

    /**
     * Return the list of body buttons for a specific player with the view requirement applied
     * @param player the player to check
     * @return the list of body buttons
     */
    List<InputButton> getDialogInputs(Player player);

    List<Requirement> getYesActions();

    List<Requirement> getNoActions();

    void addYesAction(List<Requirement> action);

    void addNoAction(List<Requirement> action);

    void addAction(List<Requirement> action);

    List<Requirement> getActions();

    void setYesText(String yesText);

    void setNoText(String noText);

    void setYesTooltip(String yesTooltip);

    void setNoTooltip(String noTooltip);

    String getYesText();

    String getYesText(Player player);

    String getNoText();

    String getNoText(Player player);

    String getYesTooltip();

    String getYesTooltip(Player player);

    String getNoTooltip();

    String getNoTooltip(Player player);

    int getYesWidth();

    int getNoWidth();

    void setYesWidth(int yesWidth);

    void setNoWidth(int noWidth);

    String getLabel();

    String getLabel(Player player);

    void setLabel(String label);

    String getLabelTooltip();

    String getLabelTooltip(Player player);

    void setLabelTooltip(String labelTooltip);

    int getLabelWidth();

    void setLabelWidth(int labelWidth);

    List<ActionButtonRecord> getActionButtons(Player player);

    List<ActionButtonRecord> getActionButtons();

    void addActionButton(ActionButtonRecord actionButton);

    int getNumberOfColumns();

    void setNumberOfColumns(int numberOfColumns);

    ZDialogInventoryBuild getBuild(Player player);

    void setActionButtonServerLink(ActionButtonRecord actionButtonRecord);

    ActionButtonRecord getActionButtonServerLink(Player player);

    ActionButtonRecord getActionButtonServerLink();

    void setOpenRequirement(Requirement openRequirement);

    Requirement getOpenRequirement();

    boolean hasOpenRequirement(Player player);
}
