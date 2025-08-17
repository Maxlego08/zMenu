package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogType;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import fr.maxlego08.menu.hooks.dialogs.utils.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.utils.record.ZDialogInventoryBuild;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public interface ZDialogs {
    String getName(Player player);

    String getExternalTitle();

    String getFileName();

    Plugin getPlugin();

    File getFile();

    void setFile(File file);

    boolean canCloseWithEscape();

    void setCanCloseWithEscape(boolean canCloseWithEscape);

    DialogBase.DialogAfterAction getAfterAction();

    void setAfterAction(DialogBase.DialogAfterAction afterAction);

    boolean isPause();

    void setPause(boolean pause);

    void setDialogType(DialogType dialogType);

    DialogType getDialogType();

    List<BodyButton> getBodyButtons();

    List<InputButton> getInputButtons();

    void setBodyButtons(List<BodyButton> bodyButtons);

    void setInputButtons(List<InputButton> inputButtons);

    List<DialogBody> getDialogBodies(Player player);

    List<DialogInput> getDialogInputs(Player player);

    List<DialogAction> getYesActions();

    List<DialogAction> getNoActions();

    void addYesAction(DialogAction action);

    void addNoAction(DialogAction action);

    void addAction(DialogAction action);

    List<DialogAction> getActions();

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
}
