package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
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
        trimPatterns.put("vex", TrimPattern.VEX);
        trimPatterns.put("wild", TrimPattern.WILD);
        trimPatterns.put("coast", TrimPattern.COAST);
        trimPatterns.put("dune", TrimPattern.DUNE);
        trimPatterns.put("wayfinder", TrimPattern.WAYFINDER);
        trimPatterns.put("raiser", TrimPattern.RAISER);
        trimPatterns.put("shaper", TrimPattern.SHAPER);
        trimPatterns.put("host", TrimPattern.HOST);
        trimPatterns.put("ward", TrimPattern.WARD);
        trimPatterns.put("silence", TrimPattern.SILENCE);
        trimPatterns.put("tide", TrimPattern.TIDE);
        trimPatterns.put("snout", TrimPattern.SNOUT);
        trimPatterns.put("rib", TrimPattern.RIB);
        trimPatterns.put("eye", TrimPattern.EYE);
        trimPatterns.put("spire", TrimPattern.SPIRE);
        if (NmsVersion.getCurrentVersion().isNewItemStackAPI()){
            trimPatterns.put("bolt", TrimPattern.BOLT);
            trimPatterns.put("flow", TrimPattern.FLOW);
        }

    }

    public Map<String, TrimMaterial> getTrimMaterials() {
        return trimMaterials;
    }

    public Map<String, TrimPattern> getTrimPatterns() {
        return trimPatterns;
    }
}
