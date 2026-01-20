package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BlocksAttacksComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.itemstack.ZDamageReductionRecord;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotBlocksAttacksItemComponentLoader extends ItemComponentLoader {

    public SpigotBlocksAttacksItemComponentLoader(){
        super("blocks_attacks");
    }

    private @Nullable Sound getSoundFromSection(@NotNull ConfigurationSection section, @NotNull String keyName) {
        String soundString = section.getString(keyName);
        if (soundString != null) {
            try {
                NamespacedKey key = NamespacedKey.fromString(soundString);
                if (key != null) {
                    return Registry.SOUNDS.getOrThrow(key);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    private float parseFloatField(Map<String, Object> map, String key, float defaultValue, String errorLog) {
        Object valueObj = map.get(key);
        if (valueObj instanceof Number number) {
            return number.floatValue();
        } else if (valueObj instanceof String str) {
            try {
                return Float.parseFloat(str);
            } catch (NumberFormatException ignored) {
            }
        }
        if (Configuration.enableDebug && errorLog != null) {
            Logger.info(errorLog);
        }
        return defaultValue;
    }

    private void addDamageType(List<DamageType> damageTypes, String typeString) {
        try {
            NamespacedKey key = NamespacedKey.fromString(typeString.toUpperCase());
            if (key != null) {
                damageTypes.add(Registry.DAMAGE_TYPE.getOrThrow(key));
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void loadDamageReductionRecords(@NotNull ConfigurationSection componentSection, @NotNull List<ZDamageReductionRecord> damageReductionRecords) {
        List<Map<?, ?>> mapList = componentSection.getMapList("damage_reductions");
        for (var rawMap : mapList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            Object type = map.get("type");
            if (type == null) continue;
            List<DamageType> damageTypes = new ArrayList<>();
            if (type instanceof String typeString) {
                addDamageType(damageTypes, typeString);
            } else if (type instanceof List<?> typeList) {
                for (Object obj : typeList) {
                    if (obj instanceof String typeString) {
                        addDamageType(damageTypes, typeString);
                    }
                }
            }
            if (damageTypes.isEmpty()) continue;
            float base = parseFloatField(map, "base", 0f, "Invalid base value for BlocksAttacksComponent damage reduction, using default 0f.");
            float factor = parseFloatField(map, "factor", 0f, "Invalid factor value for BlocksAttacksComponent damage reduction, using default 0f.");
            float horizontalBlockingAngle = parseFloatField(map, "horizontal_blocking_angle", 90f, "Invalid horizontal_blocking_angle value for BlocksAttacksComponent damage reduction, using default 90f.");
            damageReductionRecords.add(new ZDamageReductionRecord(
                    damageTypes,
                    base,
                    factor,
                    horizontalBlockingAngle
            ));
        }
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            return null;
        }
        double blockDelaySeconds = componentSection.getDouble("block_delay_seconds", 0f);
        double disableCooldownScale = componentSection.getDouble("disable_cooldown_scale", 1d);
        double itemDamageThreshold = componentSection.getDouble("item_damage.threshold", 0f);
        double itemDamageBase = componentSection.getDouble("item_damage.base", 0f);
        double itemDamageFactor = componentSection.getDouble("item_damage.factor", 1.5f);
        List<ZDamageReductionRecord> damageReductionRecords = new ArrayList<>();
        loadDamageReductionRecords(componentSection, damageReductionRecords);
        Sound blockSound = getSoundFromSection(componentSection, "block_sound");
        Sound disabledSound = getSoundFromSection(componentSection, "disabled_sound");
        return new BlocksAttacksComponent(
                (float) blockDelaySeconds,
                (float) disableCooldownScale,
                damageReductionRecords,
                (float) itemDamageThreshold,
                (float) itemDamageBase,
                (float) itemDamageFactor,
                blockSound,
                disabledSound
        );
    }
}
