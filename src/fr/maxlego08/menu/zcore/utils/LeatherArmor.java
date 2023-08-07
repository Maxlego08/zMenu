package fr.maxlego08.menu.zcore.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public class LeatherArmor {
    private ArmorType type;
    private Color color;
    
    public LeatherArmor(ArmorType type, Color color){
        this.type = type;
        this.color = color;
    }

    public ArmorType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setType(ArmorType type) {
        this.type = type;
    }

    @NotNull
    public ItemStack toItemStack(int amount){
        Material material = null;
        switch (type) {
            case HELMET:
                material = Material.LEATHER_HELMET;
                break;
            case CHESTPLATE:
                material = Material.LEATHER_CHESTPLATE;
                break;
            case LEGGINGS:
                material = Material.LEATHER_LEGGINGS;
                break;
            case BOOTS:
                material = Material.LEATHER_BOOTS;
                break;
            case HORSE_ARMOR:
                material = Material.LEATHER_HORSE_ARMOR;
                break;
        }
        ItemStack stack = new ItemStack(material, amount);
        LeatherArmorMeta lam = (LeatherArmorMeta) stack.getItemMeta();
        lam.setColor(color);
        stack.setItemMeta(lam);
        return stack;
    }
    
    public enum ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        HORSE_ARMOR
    }
}
