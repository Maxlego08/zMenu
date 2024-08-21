package fr.maxlego08.menu.api.utils;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.HashMap;
import java.util.Map;

public class TrimHelper {

    private final Map<String, TrimMaterial> trimMaterials = new HashMap<>();
    private final Map<String, TrimPattern> trimPatterns = new HashMap<>();

    public TrimHelper() {

        trimMaterials.put("quartz", TrimMaterial.QUARTZ);
        trimMaterials.put("iron", TrimMaterial.IRON);
        trimMaterials.put("netherite", TrimMaterial.NETHERITE);
        trimMaterials.put("redstone", TrimMaterial.REDSTONE);
        trimMaterials.put("copper", TrimMaterial.COPPER);
        trimMaterials.put("gold", TrimMaterial.GOLD);
        trimMaterials.put("emerald", TrimMaterial.EMERALD);
        trimMaterials.put("diamond", TrimMaterial.DIAMOND);
        trimMaterials.put("lapis", TrimMaterial.LAPIS);
        trimMaterials.put("amethyst", TrimMaterial.AMETHYST);

        trimPatterns.put("sentry", TrimPattern.SENTRY);
        trimPatterns.put("dune", TrimPattern.DUNE);
        trimPatterns.put("coast", TrimPattern.COAST);
        trimPatterns.put("wild", TrimPattern.WILD);
        trimPatterns.put("ward", TrimPattern.WARD);
        trimPatterns.put("eye", TrimPattern.EYE);
        trimPatterns.put("vex", TrimPattern.VEX);
        trimPatterns.put("tide", TrimPattern.TIDE);
        trimPatterns.put("snout", TrimPattern.SNOUT);
        trimPatterns.put("rib", TrimPattern.RIB);
        trimPatterns.put("spire", TrimPattern.SPIRE);
        trimPatterns.put("wayfinder", TrimPattern.WAYFINDER);
        trimPatterns.put("shaper", TrimPattern.SHAPER);
        trimPatterns.put("silence", TrimPattern.SILENCE);
        trimPatterns.put("raiser", TrimPattern.RAISER);
        trimPatterns.put("host", TrimPattern.HOST);
    }

    public Map<String, TrimMaterial> getTrimMaterials() {
        return trimMaterials;
    }

    public Map<String, TrimPattern> getTrimPatterns() {
        return trimPatterns;
    }
}
