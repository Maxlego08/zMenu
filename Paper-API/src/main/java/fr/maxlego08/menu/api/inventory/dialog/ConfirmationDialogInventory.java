package fr.maxlego08.menu.api.inventory.dialog;

import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConfirmationDialogInventory extends DialogInventory {
    void addYesRequirements(@NotNull List<Requirement> requirements);
    void addYesRequirement(@NotNull Requirement requirement);
    void addNoRequirements(@NotNull List<Requirement> requirements);
    void addNoRequirement(@NotNull Requirement requirement);

    ActionButtonRecord getYesActionButtonRecord();
    ActionButtonRecord getNoActionButtonRecord();

    List<Requirement> getYesRequirements();

    List<Requirement> getNoRequirements();

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
