package fr.maxlego08.menu.api.itemstack;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimConfiguration {
    private boolean enable;
    private TrimMaterial material;
    private TrimPattern pattern;

    // Constructeur
    public TrimConfiguration(boolean enable, TrimMaterial material, TrimPattern pattern) {
        this.enable = enable;
        this.material = material;
        this.pattern = pattern;
    }

    // Getters
    public boolean isEnable() {
        return enable;
    }

    public TrimMaterial getMaterial() {
        return material;
    }

    public TrimPattern getPattern() {
        return pattern;
    }

    // Setters
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setMaterial(TrimMaterial material) {
        this.material = material;
    }

    public void setPattern(TrimPattern pattern) {
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