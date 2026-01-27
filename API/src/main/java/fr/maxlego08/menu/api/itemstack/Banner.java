package fr.maxlego08.menu.api.itemstack;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Banner {
    private DyeColor baseColor;

    private List<Pattern> patterns;

    public Banner(@NotNull DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public Banner(@NotNull DyeColor baseColor,@NotNull List<Pattern> patterns) {
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    @Contract(pure = true)
    @NotNull
    public DyeColor getBaseColor() {
        return baseColor;
    }

    @Contract(pure = true)
    @NotNull
    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setBaseColor(@NotNull DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public void setPatterns(@NotNull List<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Contract(pure = true)
    @NotNull
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
