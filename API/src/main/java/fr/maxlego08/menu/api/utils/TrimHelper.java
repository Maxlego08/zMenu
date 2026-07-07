package fr.maxlego08.menu.api.utils;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility/helper for looking up available trim materials and patterns by name for item customization.
 */
public class TrimHelper {

    private final Map<String, TrimMaterial> trimMaterials = new HashMap<>();
    private final Map<String, TrimPattern> trimPatterns = new HashMap<>();

    public TrimHelper() {

        this.trimMaterials.put("quartz", TrimMaterial.QUARTZ);
        this.trimMaterials.put("iron", TrimMaterial.IRON);
        this.trimMaterials.put("netherite", TrimMaterial.NETHERITE);
        this.trimMaterials.put("redstone", TrimMaterial.REDSTONE);
        this.trimMaterials.put("copper", TrimMaterial.COPPER);
        this.trimMaterials.put("gold", TrimMaterial.GOLD);
        this.trimMaterials.put("emerald", TrimMaterial.EMERALD);
        this.trimMaterials.put("diamond", TrimMaterial.DIAMOND);
        this.trimMaterials.put("lapis", TrimMaterial.LAPIS);
        this.trimMaterials.put("amethyst", TrimMaterial.AMETHYST);
        this.trimMaterials.put("resin", TrimMaterial.RESIN);

        this.trimPatterns.put("sentry", TrimPattern.SENTRY);
        this.trimPatterns.put("dune", TrimPattern.DUNE);
        this.trimPatterns.put("coast", TrimPattern.COAST);
        this.trimPatterns.put("wild", TrimPattern.WILD);
        this.trimPatterns.put("ward", TrimPattern.WARD);
        this.trimPatterns.put("eye", TrimPattern.EYE);
        this.trimPatterns.put("vex", TrimPattern.VEX);
        this.trimPatterns.put("tide", TrimPattern.TIDE);
        this.trimPatterns.put("snout", TrimPattern.SNOUT);
        this.trimPatterns.put("rib", TrimPattern.RIB);
        this.trimPatterns.put("spire", TrimPattern.SPIRE);
        this.trimPatterns.put("wayfinder", TrimPattern.WAYFINDER);
        this.trimPatterns.put("shaper", TrimPattern.SHAPER);
        this.trimPatterns.put("silence", TrimPattern.SILENCE);
        this.trimPatterns.put("raiser", TrimPattern.RAISER);
        this.trimPatterns.put("host", TrimPattern.HOST);
    }

    @NotNull
    public Map<String, TrimMaterial> getTrimMaterials() {
        return this.trimMaterials;
    }

    @NotNull
    public Map<String, TrimPattern> getTrimPatterns() {
        return this.trimPatterns;
    }
}
