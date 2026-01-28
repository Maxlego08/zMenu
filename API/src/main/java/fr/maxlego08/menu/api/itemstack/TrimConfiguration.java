package fr.maxlego08.menu.api.itemstack;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TrimConfiguration {
    private boolean enable;
    private TrimMaterial material;
    private TrimPattern pattern;

    // Constructeur
    public TrimConfiguration(boolean enable, @NotNull TrimMaterial material,@NotNull TrimPattern pattern) {
        this.enable = enable;
        this.material = material;
        this.pattern = pattern;
    }

    // Getters
    @Contract(pure = true)
    public boolean isEnable() {
        return enable;
    }

    @Contract(pure = true)
    @NotNull
    public TrimMaterial getMaterial() {
        return material;
    }

    @Contract(pure = true)
    @NotNull
    public TrimPattern getPattern() {
        return pattern;
    }

    // Setters
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setMaterial(@NotNull TrimMaterial material) {
        this.material = material;
    }

    public void setPattern(@NotNull TrimPattern pattern) {
        this.pattern = pattern;
    }

    // toString method
    @Override
    public String toString() {
        return "TrimConfiguration{" +
                "enable=" + enable +
                ", material=" + material +
                ", pattern=" + pattern +
                '}';
    }
}