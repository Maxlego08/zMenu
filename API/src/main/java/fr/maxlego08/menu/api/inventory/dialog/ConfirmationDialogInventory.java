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
}
