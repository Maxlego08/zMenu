package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.dialogs.record.ZDialogInventoryBuild;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface DialogInventory extends Inventory{
    String getExternalTitle();

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

    //TODO getOpenWithItem not implemented but need
    @Override
    default OpenWithItem getOpenWithItem() {
        return null;
    }

    /**
     * Set unUsed méthod of Inventory Interface, don't used in BedrockInventory
     *
     */

    @Override
    default int size() {
        return 9;
    }

    @Override
    default InventoryType getType() {
        return null;
    }

    @Override
    default boolean shouldCancelItemPickup() {
        return false;
    }

    @Override
    default Collection<Button> getButtons() {
        return Collections.emptyList();
    }

    @Override
    default Collection<Pattern> getPatterns() {
        return Collections.emptyList();
    }

    @Override
    default <T extends Button> List<T> getButtons(Class<T> type) {
        return Collections.emptyList();
    }

    @Override
    default int getMaxPage(Collection<Pattern> patterns, Player player, Object... objects) {
        return 1;
    }

    @Override
    default List<Button> sortButtons(int page, Object... objects) {
        return Collections.emptyList();
    }

    @Override
    default List<Button> sortPatterns(Pattern pattern, int page, Object... objects) {
        return Collections.emptyList();
    }

    @Override
    default InventoryResult openInventory(Player player, InventoryEngine InventoryEngine) {
        return null;
    }

    @Override
    default void postOpenInventory(Player player, InventoryEngine InventoryEngine) {};

    @Override
    default void closeInventory(Player player, InventoryEngine InventoryEngine) {}

    @Override
    default MenuItemStack getFillItemStack() {
        return null;
    }

    @Override
    default int getUpdateInterval() {
        return 0;
    }

    @Override
    default boolean cleanInventory() {
        return false;
    }

    @Override
    default Map<String, String> getTranslatedNames() {
        return null;
    }
}
