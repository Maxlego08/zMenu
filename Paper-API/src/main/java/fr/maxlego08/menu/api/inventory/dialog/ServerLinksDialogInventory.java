package fr.maxlego08.menu.api.inventory.dialog;

import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ServerLinksDialogInventory extends DialogInventory {

    void setExitActionButton(ActionButtonRecord actionButtonRecord);

    ActionButtonRecord getExitActionButton(@NotNull Player player);

    ActionButtonRecord getExitActionButton();

}
