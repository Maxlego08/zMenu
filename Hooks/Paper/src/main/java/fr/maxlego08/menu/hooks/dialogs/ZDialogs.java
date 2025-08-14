package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogType;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import fr.maxlego08.menu.hooks.dialogs.utils.record.ZDialogInventoryBuild;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public interface ZDialogs {

    /**
     * Get the name of the dialog, parsed for the target {@linkplain Player}.
     * @param player
     * @return String
     */
    String getName(Player player);


    String getExternalTitle();

    /**
     * Get the name of the dialog file.
     * This is the name of the file without the ".yml" extension.
     * @return String
     */
    String getFileName();

    /**
     * Get the plugin that owns this dialog.
     * @return Plugin
     */
    Plugin getPlugin();

    /**
     * Get the file where this dialog is stored.
     * @return File
     */
    File getFile();

    /**
     * Set the file where this dialog is stored.
     * @param file File
     */
    void setFile(File file);

    /**
     * Check if the dialog can be closed with the escape key.
     * @return boolean
     */
    boolean canCloseWithEscape();

    /**
     * Set if the dialog can be closed with the escape key.
     * @param canCloseWithEscape boolean
     */
    void setCanCloseWithEscape(boolean canCloseWithEscape);

    /**
     * Get the action to perform after the dialog is closed.
     * @return DialogBase.DialogAfterAction
     */
    DialogBase.DialogAfterAction getAfterAction();

    /**
     * Set the action to perform after the dialog is closed.
     * @param afterAction
     */
    void setAfterAction(DialogBase.DialogAfterAction afterAction);

    /**
     * Check if when the dialog is opened, the game should be paused.
     * @return boolean
     */
    boolean isPause();

    /**
     * Set if when the dialog is opened, the game should be paused.
     * @param pause boolean
     */
    void setPause(boolean pause);

    /**
     * Get the type of the dialog.
     * @param dialogType DialogType
     */
    void setDialogType(DialogType dialogType);

    /**
     * Get the type of the dialog.
     * @return DialogType
     */
    DialogType getDialogType();

    /**
     * Get all the body buttons of the dialog.
     * These buttons are displayed in the body of the dialog.
     * @return List<BodyButton>
     */
    List<BodyButton> getBodyButtons();

    /**
     * Get all the input buttons of the dialog.
     * These buttons are displayed in the input section of the dialog. (After the body)
     * @return List<InputButton>
     */
    List<InputButton> getInputButtons();

    /**
     * Set the body buttons of the dialog.
     * @param bodyButtons List<BodyButton>
     */
    void setBodyButtons(List<BodyButton> bodyButtons);

    /**
     * Set the input buttons of the dialog.
     * @param inputButtons List<InputButton>
     */
    void setInputButtons(List<InputButton> inputButtons);

    /**
     * Get the dialog bodies for the given player.
     * These bodies are already parsed for the player.
     * @param player Player
     * @return List<DialogBody>
     */
    List<DialogBody> getDialogBodies(Player player);

    /**
     * Get the dialog inputs for the given player.
     * These inputs are already parsed for the player.
     * @param player Player
     * @return List<DialogInput>
     */
    List<DialogInput> getDialogInputs(Player player);

    /**
     * Get the yes actions of the dialog.
     * These actions are executed when the player clicks on the "Yes" button. Only when the {@link DialogType} is {@link DialogType#CONFIRMATION}.
     * @return List<DialogAction>
     */
    List<DialogAction> getYesActions();

    /**
     * Get the no actions of the dialog.
     * These actions are executed when the player clicks on the "No" button. Only when the {@link DialogType} is {@link DialogType#CONFIRMATION}.
     * @return List<DialogAction>
     */
    List<DialogAction> getNoActions();

    /**
     * Add a yes action to the dialog.
     * This action is executed when the player clicks on the "Yes" button. Only when the {@link DialogType} is {@link DialogType#CONFIRMATION}.
     * @param action DialogAction
     */
    void addYesAction(DialogAction action);

    /**
     * Add a no action to the dialog.
     * This action is executed when the player clicks on the "No" button. Only when the {@link DialogType} is {@link DialogType#CONFIRMATION}.
     * @param action DialogAction
     */
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




    /**
     * Get the build of the dialog.
     * This is used to build the dialog inventory.
     * @param player Player
     * @return ZDialogInventoryBuild
     */
    ZDialogInventoryBuild getBuild(Player player);



}
