package fr.maxlego08.menu.api.enums;

import org.bukkit.inventory.ItemRarity;

public enum MenuItemRarity {

    COMMON,
    UNCOMMON,
    RARE,
    EPIC;

    public ItemRarity getItemRarity() {
        switch (this) {
            case COMMON:
                return ItemRarity.COMMON;
            case UNCOMMON:
                return ItemRarity.UNCOMMON;
            case RARE:
                return ItemRarity.RARE;
            case EPIC:
                return ItemRarity.EPIC;
        }
        return ItemRarity.COMMON;
    }

}
