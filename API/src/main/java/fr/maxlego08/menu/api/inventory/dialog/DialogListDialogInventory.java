package fr.maxlego08.menu.api.inventory.dialog;

import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.api.utils.record.dialogs.DialogListBackButton;
import fr.maxlego08.menu.api.utils.record.dialogs.DialogListEntry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A dialog that shows one button per referenced dialog. Each button is labelled with the
 * {@code external-title} of its dialog (or its name when it has none) and opens that dialog.
 * <p>
 * Referenced dialogs are resolved and built when the list is opened. Dialogs whose
 * {@code open-requirement} the player does not meet are hidden from the list.
 */
public interface DialogListDialogInventory extends DialogInventory {

    @NotNull
    List<DialogListEntry> getEntries();

    void addEntry(@NotNull DialogListEntry entry);

    int getNumberOfColumns();

    void setNumberOfColumns(int numberOfColumns);

    int getButtonWidth();

    void setButtonWidth(int buttonWidth);

    @NotNull
    DialogListBackButton getBackButton();

    void setBackButton(@NotNull DialogListBackButton backButton);

    void setExitActionButton(@Nullable ActionButtonRecord actionButtonRecord);

    @Nullable
    ActionButtonRecord getExitActionButton(@NotNull Player player);

    @Nullable
    ActionButtonRecord getExitActionButton();

}
