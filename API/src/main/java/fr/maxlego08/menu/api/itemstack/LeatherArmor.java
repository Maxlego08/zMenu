package fr.maxlego08.menu.api.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LeatherArmor {
    private ArmorType type;
    private Color color;

    public LeatherArmor(@NotNull ArmorType type,@NotNull Color color) {
        this.type = type;
        this.color = color;
    }

    @Contract(pure = true)
    @NotNull
    public ArmorType getType() {
        return type;
    }

    public void setType(@NotNull ArmorType type) {
        this.type = type;
    }

    @Contract(pure = true)
    @NotNull
    public Color getColor() {
        return color;
    }

    public void setColor(@NotNull Color color) {
        this.color = color;
    }

    @NotNull
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
        if (lam != null) {
            lam.setColor(color);
        }
        stack.setItemMeta(lam);
        return stack;
    }

    public enum ArmorType {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS, HORSE_ARMOR
    }
}
