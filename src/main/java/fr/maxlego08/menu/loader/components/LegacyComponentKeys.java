package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.utils.SectionSerializable;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the old snake_case component keys working now that components use kebab-case keys.
 * <p>
 * Before the component loaders run, the old keys of an item's {@code components} section are renamed
 * to their kebab-case form, in the loaded configuration only, and a warning is logged once per file
 * and key. Dropping the old keys later means deleting this class and its call.
 */
public final class LegacyComponentKeys {
    private static final Set<String> SNAKE_CASE_KEYS = Set.of(
            // kinetic-weapon
            "delay_ticks", "contact_cooldown_ticks", "dismount_conditions", "knockback_conditions", "damage_conditions",
            "forward_movement", "damage_multiplier", "hit_sound", "max_duration_ticks", "min_speed", "min_relative_speed",
            // blocks-attacks
            "bypassed_by", "item_damage", "damage_reductions", "horizontal_blocking_angle",
            // firework effects
            "fade_colors", "has_trail", "has_twinkle",
            // potion effects
            "show_particles", "show_icon",
            // tooltip-display
            "hide_tooltip", "hidden_components",
            // death-protection
            "death_effects",
            // tool, as written in Minecraft
            "default_mining_speed", "damage_per_block", "can_destroy_blocks_in_creative", "correct_for_drops"
    );

    private static final Set<String> DATA_COMPONENTS = Set.of(
            "custom-data",
            "block-state",
            "enchantments",
            "stored-enchantments",
            "map-decorations");

    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

    private LegacyComponentKeys() {
    }

    /**
     * Renames the old keys of every component of an item.
     *
     * @param components The {@code components} section of the item.
     * @param file       The file the item comes from, named in the warning.
     */
    public static void normalize(ConfigurationSection components, File file) {
        for (String componentName : components.getKeys(false)) {
            if (DATA_COMPONENTS.contains(componentName)) continue;
            Object value = components.get(componentName);
            if (value instanceof ConfigurationSection section) {
                renameSection(section, file, componentName);
            } else if (value instanceof List<?> list) {
                components.set(componentName, renameList(list, file, componentName));
            }
        }
    }

    private static void renameSection(ConfigurationSection section, File file, String componentName) {
        for (String key : new ArrayList<>(section.getKeys(false))) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection child) {
                renameSection(child, file, componentName);
            } else if (value instanceof List<?> list) {
                value = renameList(list, file, componentName);
                section.set(key, value);
            }

            String newKey = renamedKey(key, file, componentName);
            if (newKey.equals(key)) continue;
            section.set(key, null);
            if (section.contains(newKey)) continue;
            if (value instanceof ConfigurationSection child) section.createSection(newKey, SectionSerializable.toMap(child));
            else section.set(newKey, value);
        }
    }

    private static List<Object> renameList(List<?> list, File file, String componentName) {
        List<Object> renamed = new ArrayList<>(list.size());
        for (Object element : list) {
            renamed.add(renameValue(element, file, componentName));
        }
        return renamed;
    }

    private static Object renameValue(Object value, File file, String componentName) {
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> renamed = new LinkedHashMap<>();
            map.forEach((key, child) -> {
                Object newKey = key instanceof String stringKey ? renamedKey(stringKey, file, componentName) : key;
                if (!renamed.containsKey(newKey)) renamed.put(newKey, renameValue(child, file, componentName));
            });
            return renamed;
        }
        if (value instanceof List<?> list) return renameList(list, file, componentName);
        return value;
    }

    private static String renamedKey(String key, File file, String componentName) {
        if (!SNAKE_CASE_KEYS.contains(key)) return key;
        String newKey = key.replace('_', '-');
        if (WARNED.add(file.getPath() + ":" + key)) {
            Logger.info("The key " + key + " of the component " + componentName + " in " + file.getName() + " is deprecated, use " + newKey + " instead.", Logger.LogType.WARNING);
        }
        return newKey;
    }
}
