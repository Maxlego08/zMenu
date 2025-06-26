package fr.maxlego08.menu.api.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherArmor {
    private ArmorType type;
    private Color color;

    public LeatherArmor(ArmorType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public ArmorType getType() {
        return type;
    }

    public void setType(ArmorType type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ItemStack toItemStack(int amount) {
        Material material = switch (type) {
            case HELMET -> Material.LEATHER_HELMET;
            case CHESTPLATE -> Material.LEATHER_CHESTPLATE;
            case LEGGINGS -> Material.LEATHER_LEGGINGS;
            case BOOTS -> Material.LEATHER_BOOTS;
            case HORSE_ARMOR -> Material.LEATHER_HORSE_ARMOR;
        };
        ItemStack stack = new ItemStack(material, amount);
        LeatherArmorMeta lam = (LeatherArmorMeta) stack.getItemMeta();
        lam.setColor(color);
        stack.setItemMeta(lam);
        return stack;
    }

    public enum ArmorType {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS, HORSE_ARMOR
    }
}
