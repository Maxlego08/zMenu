package fr.maxlego08.menu.api.inventory.dialog;

import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import org.bukkit.entity.Player;

public interface ConfirmationDialogInventory extends DialogInventory {

    ActionButtonRecord getYesActionButtonRecord();
    ActionButtonRecord getNoActionButtonRecord();

    void setYesText(String yesText);

    void setNoText(String noText);

    void setYesTooltip(String yesTooltip);

    void setNoTooltip(String noTooltip);

    String getYesText();

    /**
     * @return the "yes" label with PlaceholderAPI read for the player
     * @deprecated render {@code getYesActionButtonRecord().labelTemplate()} for the player.
     */
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    String getYesText(Player player);

    String getNoText();

    /**
     * @return the "no" label with PlaceholderAPI read for the player
     * @deprecated render {@code getNoActionButtonRecord().labelTemplate()} for the player.
     */
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    String getNoText(Player player);

    String getYesTooltip();

    /**
     * @return the "yes" tooltip with PlaceholderAPI read for the player
     * @deprecated render {@code getYesActionButtonRecord().tooltipTemplate()} for the player.
     */
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    String getYesTooltip(Player player);

    String getNoTooltip();

    /**
     * @return the "no" tooltip with PlaceholderAPI read for the player
     * @deprecated render {@code getNoActionButtonRecord().tooltipTemplate()} for the player.
     */
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    String getNoTooltip(Player player);

    int getYesWidth();

    int getNoWidth();

    void setYesWidth(int yesWidth);

    void setNoWidth(int noWidth);
}
