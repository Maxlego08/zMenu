package fr.maxlego08.menu.api.enums;

import org.bukkit.inventory.ItemRarity;

public enum MenuItemRarity {

    COMMON,
    UNCOMMON,
    RARE,
    EPIC;

    public ItemRarity getItemRarity() {
        return switch (this) {
            case COMMON -> ItemRarity.COMMON;
            case UNCOMMON -> ItemRarity.UNCOMMON;
            case RARE -> ItemRarity.RARE;
            case EPIC -> ItemRarity.EPIC;
        };
    }

}
