package fr.maxlego08.menu.enchantment;

import fr.maxlego08.menu.api.enchantment.Enchantments;
import fr.maxlego08.menu.api.enchantment.MenuEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZEnchantments implements Enchantments {

    private final List<MenuEnchantment> essentialsEnchantments = new ArrayList<>();

    @Override
    public Optional<MenuEnchantment> getEnchantments(String enchantment) {
        return essentialsEnchantments.stream().filter(menuEnchantment -> menuEnchantment.getAliases().stream().anyMatch(e -> e.equalsIgnoreCase(enchantment))).findFirst();
    }

    @Override
    public void register() {
        this.register(valueOf("DAMAGE_ALL", "SHARPNESS"), "DAMAGE_ALL", "SHARPNESS", "alldamage", "alldmg", "sharpness", "sharp", "dal");
        this.register(valueOf("DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS"), "DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS", "ardmg", "baneofarthropods", "baneofarthropod", "arthropod", "dar");
        this.register(valueOf("DAMAGE_UNDEAD", "SMITE"), "DAMAGE_UNDEAD", "undeaddamage", "smite", "du");
        this.register(valueOf("DIG_SPEED", "EFFICIENCY"), "DIG_SPEED", "EFFICIENCY", "digspeed", "efficiency", "minespeed", "cutspeed", "ds", "eff");
        this.register(valueOf("DURABILITY", "UNBREAKING"), "durability", "dura", "unbreaking", "d");
        this.register(Enchantment.THORNS, "thorns", "highcrit", "thorn", "highercrit", "t");
        this.register(Enchantment.FIRE_ASPECT, "FIRE_ASPECT", "fireaspect", "fire", "meleefire", "meleeflame", "fa");
        this.register(Enchantment.KNOCKBACK, "knockback", "kback", "kb", "k");
        this.register(valueOf("LOOT_BONUS_BLOCKS", "FORTUNE"), "LOOT_BONUS_BLOCKS", "FORTUNE", "blockslootbonus", "fortune", "fort", "lbb");
        this.register(valueOf("LOOT_BONUS_MOBS", "LOOTING"), "LOOT_BONUS_MOBS", "LOOTING", "mobslootbonus", "mobloot", "looting", "lbm");
        this.register(valueOf("OXYGEN", "RESPIRATION"), "OXYGEN", "RESPIRATION", "oxygen", "respiration", "breathing", "breath", "o");
        this.register(valueOf("PROTECTION_ENVIRONMENTAL", "PROTECTION"), "PROTECTION_ENVIRONMENTAL", "PROTECTION", "protection", "prot", "protect", "p");
        this.register(valueOf("PROTECTION_EXPLOSIONS", "BLAST_PROTECTION"), "PROTECTION_EXPLOSIONS", "BLAST_PROTECTION", "explosionsprotection", "explosionprotection", "expprot", "blastprotection", "bprotection", "bprotect", "blastprotect", "pe");
        this.register(valueOf("PROTECTION_FALL", "FEATHER_FALLING"), "PROTECTION_FALL", "FEATHER_FALLING", "fallprotection", "fallprot", "featherfall", "featherfalling", "pfa");
        this.register(valueOf("PROTECTION_FIRE", "FIRE_PROTECTION"), "PROTECTION_FIRE", "FIRE_PROTECTION", "fireprotection", "flameprotection", "fireprotect", "flameprotect", "fireprot", "flameprot", "pf");
        this.register(valueOf("PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION"), "PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION", "projectileprotection", "projprot", "pp");
        this.register(Enchantment.SILK_TOUCH, "SILK_TOUCH", "silktouch", "softtouch", "st");
        this.register(valueOf("WATER_WORKER", "AQUA_AFFINITY"), "WATER_WORKER", "AQUA_AFFINITY", "waterworker", "aquaaffinity", "watermine", "ww");
        this.register(valueOf("ARROW_FIRE", "FLAME"), "ARROW_FIRE", "FLAME", "firearrow", "flame", "flamearrow", "af");
        this.register(valueOf("ARROW_DAMAGE", "POWER"), "ARROW_DAMAGE", "POWER", "arrowdamage", "power", "arrowpower", "ad");
        this.register(valueOf("ARROW_KNOCKBACK", "PUNCH"), "ARROW_KNOCKBACK", "PUNCH", "arrowknockback", "arrowkb", "punch", "arrowpunch", "ak");
        this.register(valueOf("ARROW_INFINITE", "INFINITY"), "ARROW_INFINITE", "INFINITY", "infinitearrows", "infarrows", "infinity", "infinite", "unlimited", "unlimitedarrows", "ai");
        this.register(valueOf("LUCK", "LUCK_OF_THE_SEA"), "LUCK", "LUCK_OF_THE_SEA", "luck", "luckofsea", "luckofseas", "rodluck");
        this.register(Enchantment.getByName("LURE"), "lure", "rodlure");
        this.register(Enchantment.getByName("DEPTH_STRIDER"), "DEPTH_STRIDER", "depthstrider", "depth", "strider");
        this.register(Enchantment.getByName("FROST_WALKER"), "FROST_WALKER", "frostwalker", "frost", "walker");
        this.register(Enchantment.getByName("MENDING"), "mending");
        this.register(Enchantment.getByName("BINDING_CURSE"), "BINDING_CURSE", "bindingcurse", "bindcurse", "binding", "bind");
        this.register(Enchantment.getByName("VANISHING_CURSE"), "VANISHING_CURSE", "vanishingcurse", "vanishcurse", "vanishing", "vanish");
        this.register(Enchantment.getByName("SWEEPING_EDGE"), "SWEEPING_EDGE", "sweepingedge", "sweepedge", "sweeping");
        this.register(Enchantment.getByName("LOYALTY"), "loyalty", "loyal", "return");
        this.register(Enchantment.getByName("IMPALING"), "impaling", "impale", "oceandamage", "oceandmg");
        this.register(Enchantment.getByName("RIPTIDE"), "riptide", "rip", "tide", "launch");
        this.register(Enchantment.getByName("CHANNELING"), "channelling", "chanelling", "channeling", "chaneling", "channel");
        this.register(Enchantment.getByName("MULTISHOT"), "multishot", "tripleshot");
        this.register(Enchantment.getByName("QUICK_CHARGE"), "QUICK_CHARGE", "quickcharge", "quickdraw", "fastcharge", "fastdraw");
        this.register(Enchantment.getByName("PIERCING"), "piercing");
        this.register(Enchantment.getByName("SOUL_SPEED"), "SOUL_SPEED", "soulspeed", "soilspeed", "sandspeed");
        this.register(Enchantment.getByName("SWIFT_SNEAK"), "swiftsneak", "SWIFT_SNEAK");

        // 1.21
        this.register(Enchantment.getByName("BREACH"), "breach");
        this.register(Enchantment.getByName("DENSITY"), "density");
        this.register(Enchantment.getByName("WIND_BURST"), "WIND_BURST", "windburst", "wind", "burst");
    }

    private void register(Enchantment enchantment, String... strings) {
        if (enchantment == null) return;
        try {
            this.essentialsEnchantments.add(new ZMenuEnchantment(enchantment, Arrays.asList(strings)));
        } catch (Exception ignored) {
        }
    }

    @Override
    public List<String> getEnchantments() {
        return this.essentialsEnchantments.stream().map(MenuEnchantment::getAliases).flatMap(List::stream).collect(Collectors.toList());
    }

    private Enchantment valueOf(String... names) {
        for (final String name : names) {
            try {
                Enchantment value = (Enchantment) Enchantment.class.getDeclaredField(name).get(null);
                if (value != null) {
                    return value;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        return null;
    }
}
