package fr.maxlego08.menu.api.inventory.dialog;

import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import org.bukkit.entity.Player;

import java.util.List;

public interface MultiActionDialogInventory extends DialogInventory {

    List<ActionButtonRecord> getActionButtons(Player player);

    List<ActionButtonRecord> getActionButtons();

    void addActionButton(ActionButtonRecord actionButton);

    int getNumberOfColumns();

    void setNumberOfColumns(int numberOfColumns);

}
