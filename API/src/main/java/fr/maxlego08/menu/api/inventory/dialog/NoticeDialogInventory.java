package fr.maxlego08.menu.api.inventory.dialog;

import org.bukkit.entity.Player;

public interface NoticeDialogInventory extends DialogInventory {

    String getLabel();

    String getLabel(Player player);

    void setLabel(String label);

    String getLabelTooltip();

    String getLabelTooltip(Player player);

    void setLabelTooltip(String labelTooltip);

    int getLabelWidth();

    void setLabelWidth(int labelWidth);

}
