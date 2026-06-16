package fr.maxlego08.menu.api.inventory.dialog;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.record.dialogs.ZDialogInventoryBuild;
import io.papermc.paper.dialog.Dialog;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface DialogInventory extends Inventory {

    Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent);

    @NotNull
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

    /**
     * @return Empty list of requirements
     * @deprecated Use:
     * <pre>{@code
     *     if (dialogInventory instanceof ConfirmationDialogInventory confirmationDialogInventory) {
     *         List<Requirement> noRequirements = confirmationDialogInventory.getNoActionButtonRecord().actions();
     *     }
     * }</pre>

     */
    @Deprecated(since = "1.1.1.5")
    List<Requirement> getYesActions();

    /**
     * @return Empty list of requirements
     * @deprecated Use:
     * <pre>{@code
     *     if (dialogInventory instanceof ConfirmationDialogInventory confirmationDialogInventory) {
     *         List<Requirement> noRequirements = confirmationDialogInventory.getNoActionButtonRecord().actions();
     *     }
     * }</pre>

     */
    @Deprecated(since = "1.1.1.5")
    List<Requirement> getNoActions();

    /**
     * @deprecated Use:
     * <pre>{@code
     *     if (dialogInventory instanceof ConfirmationDialogInventory confirmationDialogInventory) {
     *         confirmationDialogInventory.addYesRequirements(action);
     *     }
     * }</pre>
     */
    @Deprecated(since = "1.1.1.5")
    void addYesAction(List<Requirement> action);

    /**
     * @deprecated Use:
     * <pre>{@code
     *     if (dialogInventory instanceof ConfirmationDialogInventory confirmationDialogInventory) {
     *         confirmationDialogInventory.addNoRequirements(action);
     *     }
     * }</pre>
     */
    @Deprecated(since = "1.1.1.5")
    void addNoAction(List<Requirement> action);

    /**
     * @deprecated Use {@link NoticeDialogInventory#addAction(List)}
     */
    @Deprecated(since = "1.1.1.5")
    void addAction(List<Requirement> action);

    /**
     * @deprecated Use {@link NoticeDialogInventory#getActions()}
     */
    @Deprecated(since = "1.1.1.5")
    List<Requirement> getActions();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#setYesText(String)}
     */
    @Deprecated(since = "1.1.1.5")
    void setYesText(String yesText);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#setNoText(String)}
     */
    @Deprecated(since = "1.1.1.5")
    void setNoText(String noText);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#setYesTooltip(String)}
     */
    @Deprecated(since = "1.1.1.5")
    void setYesTooltip(String yesTooltip);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#setNoTooltip(String)}
     */
    @Deprecated(since = "1.1.1.5")
    void setNoTooltip(String noTooltip);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getYesText()}
     */
    @Deprecated(since = "1.1.1.5")
    String getYesText();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getYesText(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    String getYesText(Player player);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getNoText()}
     */
    @Deprecated(since = "1.1.1.5")
    String getNoText();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getNoText(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    String getNoText(Player player);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getYesTooltip()}
     */
    @Deprecated(since = "1.1.1.5")
    String getYesTooltip();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getYesTooltip(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    String getYesTooltip(Player player);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getNoTooltip()}
     */
    @Deprecated(since = "1.1.1.5")
    String getNoTooltip();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getNoTooltip(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    String getNoTooltip(Player player);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getYesWidth()}
     */
    @Deprecated(since = "1.1.1.5")
    int getYesWidth();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#getNoWidth()}
     */
    @Deprecated(since = "1.1.1.5")
    int getNoWidth();

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#setYesWidth(int)}
     */
    @Deprecated(since = "1.1.1.5")
    void setYesWidth(int yesWidth);

    /**
     * @deprecated Use {@link ConfirmationDialogInventory#setNoWidth(int)}
     */
    @Deprecated(since = "1.1.1.5")
    void setNoWidth(int noWidth);

    /**
     * @deprecated Use {@link NoticeDialogInventory#getLabel()}
     */
    @Deprecated(since = "1.1.1.5")
    String getLabel();

    /**
     * @deprecated Use {@link NoticeDialogInventory#getLabel(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    String getLabel(Player player);

    /**
     * @deprecated Use {@link NoticeDialogInventory#setLabel(String)}
     */
    @Deprecated(since = "1.1.1.5")
    void setLabel(String label);

    /**
     * @deprecated Use {@link NoticeDialogInventory#getLabelTooltip()}
     */
    @Deprecated(since = "1.1.1.5")
    String getLabelTooltip();

    /**
     * @deprecated Use {@link NoticeDialogInventory#getLabelTooltip(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    String getLabelTooltip(Player player);

    /**
     * @deprecated Use {@link NoticeDialogInventory#setLabelTooltip(String)}
     */
    @Deprecated(since = "1.1.1.5")
    void setLabelTooltip(String labelTooltip);

    /**
     * @deprecated Use {@link NoticeDialogInventory#getLabelWidth()}
     */
    @Deprecated(since = "1.1.1.5")
    int getLabelWidth();

    /**
     * @deprecated Use {@link NoticeDialogInventory#setLabelWidth(int)}
     */
    @Deprecated(since = "1.1.1.5")
    void setLabelWidth(int labelWidth);

    /**
     * @deprecated Use {@link MultiActionDialogInventory#getActionButtons(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    List<ActionButtonRecord> getActionButtons(Player player);

    /**
     * @deprecated Use {@link MultiActionDialogInventory#getActionButtons()}
     */
    @Deprecated(since = "1.1.1.5")
    List<ActionButtonRecord> getActionButtons();

    /**
     * @deprecated Use {@link MultiActionDialogInventory#addActionButton(ActionButtonRecord)}
     */
    @Deprecated(since = "1.1.1.5")
    void addActionButton(ActionButtonRecord actionButton);

    /**
     * @deprecated Use {@link MultiActionDialogInventory#getNumberOfColumns()}
     */
    @Deprecated(since = "1.1.1.5")
    int getNumberOfColumns();

    /**
     * @deprecated Use {@link MultiActionDialogInventory#setNumberOfColumns(int)}
     */
    @Deprecated(since = "1.1.1.5")
    void setNumberOfColumns(int numberOfColumns);

    ZDialogInventoryBuild getBuild(Player player);

    /**
     * @deprecated Use {@link ServerLinksDialogInventory#setExitActionButton(ActionButtonRecord)}
     */
    @Deprecated(since = "1.1.1.5")
    void setExitActionButton(ActionButtonRecord actionButtonRecord);


    /**
     * @deprecated Use {@link ServerLinksDialogInventory#getExitActionButton(Player)}
     */
    @Deprecated(since = "1.1.1.5")
    ActionButtonRecord getExitActionButton(@NotNull Player player);

    /**
     * @deprecated Use {@link ServerLinksDialogInventory#getExitActionButton()}
     */
    @Deprecated(since = "1.1.1.5")
    ActionButtonRecord getExitActionButton();

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
    default void postOpenInventory(Player player, InventoryEngine InventoryEngine) {}

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
