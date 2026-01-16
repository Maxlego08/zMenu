package fr.maxlego08.common.loader.components;

import fr.maxlego08.common.enums.ConsumeEffectType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractEffectItemComponentLoader extends AbstractColorItemComponentLoader {

    protected AbstractEffectItemComponentLoader(String componentName) {
        super(componentName);
    }

    protected @Nullable NamespacedKey parseNamespacedKey(String value) {
        return NamespacedKey.fromString(value.toLowerCase());
    }

    protected @Nullable ConsumeEffectType parseConsumableType(String type) {
        try {
            return ConsumeEffectType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    protected List<PotionEffectType> parsePotionEffectTypes(Object effectObj) {
        List<PotionEffectType> potionEffectTypes = new ArrayList<>();

        if (effectObj instanceof String effectString) {
            parsePotionEffectType(effectString).ifPresent(potionEffectTypes::add);
        } else if (effectObj instanceof List<?> effectList) {
            for (Object obj : effectList) {
                if (obj instanceof String effectString) {
                    parsePotionEffectType(effectString).ifPresent(potionEffectTypes::add);
                }
            }
        }

        return potionEffectTypes;
    }

    protected Optional<Sound> parseSound(String soundString) {
        try {
            NamespacedKey key = parseNamespacedKey(soundString);
            if (key == null) return Optional.empty();
            return Optional.of(Registry.SOUNDS.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    protected float parseFloat(Map<String, Object> map, String key, float defaultValue) {
        Object value = map.getOrDefault(key, defaultValue);
        if (value instanceof Number number) {
            return number.floatValue();
        }
        return defaultValue;
    }

    protected double parseDouble(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.getOrDefault(key, defaultValue);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return defaultValue;
    }

    protected Optional<PotionEffectType> parsePotionEffectType(String effectString) {
        try {
            NamespacedKey key = parseNamespacedKey(effectString);
            if (key == null) return Optional.empty();
            return Optional.of(Registry.EFFECT.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    protected List<PotionEffect> parsePotionEffects(@NotNull List<Map<?, ?>> potionEffectsRaw) {
        List<PotionEffect> potionEffects = new ArrayList<>();
        for (var rawPotionEffect : potionEffectsRaw) {
            @SuppressWarnings("unchecked")
            Map<String, Object> potionEffectMap = (Map<String, Object>) rawPotionEffect;
            parsePotionEffect(potionEffectMap).ifPresent(potionEffects::add);
        }
        return potionEffects;
    }

    protected Optional<PotionEffect> parsePotionEffect(Map<String, Object> potionEffectMap) {
        try {
            String potionEffectTypeString = (String) potionEffectMap.get("id");
            NamespacedKey key = parseNamespacedKey(potionEffectTypeString);
            if (key == null) return Optional.empty();

            PotionEffectType potionEffectType = Registry.EFFECT.getOrThrow(key);
            int duration = (int) potionEffectMap.getOrDefault("duration", 1);
            boolean amplified = (boolean) potionEffectMap.getOrDefault("amplified", false);
            byte amplifier = (byte) (amplified ? 1 : 0);
            boolean ambient = (boolean) potionEffectMap.getOrDefault("ambient", false);
            boolean particles = (boolean) potionEffectMap.getOrDefault("show_particles", true);
            boolean showIcon = (boolean) potionEffectMap.getOrDefault("show_icon", true);

            return Optional.of(new PotionEffect(
                    potionEffectType, duration, amplifier, ambient, particles, showIcon
            ));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}