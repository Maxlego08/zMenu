package fr.maxlego08.menu.zcore.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.List;

public class Banner {
    private DyeColor baseColor;

    private List<Pattern> patterns;

    public Banner(DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public Banner(DyeColor baseColor, List<Pattern> patterns) {
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    public DyeColor getBaseColor() {
        return baseColor;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setBaseColor(DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public ItemStack toItemStack(int amount){
        Material material = Material.getMaterial(baseColor.toString()+"_BANNER");
        assert material != null;
        ItemStack itemStack = new ItemStack(material, amount);
        BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
        bannerMeta.setPatterns(patterns);
        itemStack.setItemMeta(bannerMeta);
        return itemStack;
    }
}
