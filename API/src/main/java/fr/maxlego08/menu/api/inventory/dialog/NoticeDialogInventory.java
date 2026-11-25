package fr.maxlego08.menu.api.inventory.dialog;

import org.bukkit.entity.Player;

public interface NoticeDialogInventory extends DialogInventory {

    String getLabel();

    /**
     * @return the label with PlaceholderAPI read for the player
     * @deprecated render the action button's compiled label for the player.
     */
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    String getLabel(Player player);

    void setLabel(String label);

    String getLabelTooltip();

    /**
     * @return the tooltip with PlaceholderAPI read for the player
     * @deprecated render the action button's compiled tooltip for the player.
     */
    @Deprecated(since = "1.1.2.2", forRemoval = true)
    String getLabelTooltip(Player player);

    void setLabelTooltip(String labelTooltip);

    int getLabelWidth();

    void setLabelWidth(int labelWidth);

}
