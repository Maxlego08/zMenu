package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableByte;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.common.enums.ConsumeEffectType;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractEffectItemComponentLoader extends AbstractColorItemComponentLoader {

    protected AbstractEffectItemComponentLoader(String componentName) {
        super(componentName);
    }

    protected @Nullable NamespacedKey parseNamespacedKey(String value) {
        return NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
    }

    protected @Nullable ConsumeEffectType parseConsumableType(String type) {
        try {
            return ConsumeEffectType.valueOf(type.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    protected List<PotionEffectType> parsePotionEffectTypes(Object effectObj) {
        List<PotionEffectType> potionEffectTypes = new ArrayList<>();

        if (effectObj instanceof String effectString) {
            this.parsePotionEffectType(effectString).ifPresent(potionEffectTypes::add);
        } else if (effectObj instanceof List<?> effectList) {
            for (Object obj : effectList) {
                if (obj instanceof String effectString) {
                    this.parsePotionEffectType(effectString).ifPresent(potionEffectTypes::add);
                }
            }
        }

        return potionEffectTypes;
    }

    protected Optional<Sound> parseSound(String soundString) {
        try {
            NamespacedKey key = this.parseNamespacedKey(soundString);
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
            NamespacedKey key = this.parseNamespacedKey(effectString);
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
            this.parsePotionEffect(potionEffectMap).ifPresent(potionEffects::add);
        }
        return potionEffects;
    }

    protected Optional<PotionEffect> parsePotionEffect(Map<String, Object> potionEffectMap) {
        try {
            String potionEffectTypeString = (String) potionEffectMap.get("id");
            NamespacedKey key = this.parseNamespacedKey(potionEffectTypeString);
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

    protected @Nullable ResolvablePotionEffect parseResolvablePotionEffect(Map<String, Object> potionEffectMap) {
        try {
            Object idObj = potionEffectMap.get("id");
            if (!(idObj instanceof String idString)) return null;

            Resolvable<String> typeId = idString.contains("%") ? ResolvableString.ofExpression(idString) : ResolvableString.of(idString);
            ResolvableInt duration = ResolvableInt.of(potionEffectMap, "duration", 1);
            ResolvableBoolean amplified = ResolvableBoolean.of(potionEffectMap, "amplified", false);

            ResolvableByte amplifier;
            if (amplified.isDynamic()) {
                amplifier = ResolvableByte.of(amplified.getExpression());
            } else {
                amplifier = ResolvableByte.of((byte) (Boolean.TRUE.equals(amplified.getResolvedValue()) ? 1 : 0));
            }

            ResolvableBoolean ambient = ResolvableBoolean.of(potionEffectMap, "ambient", false);
            ResolvableBoolean particles = ResolvableBoolean.of(potionEffectMap, "show_particles", true);
            ResolvableBoolean showIcon = ResolvableBoolean.of(potionEffectMap, "show_icon", true);

            return new ResolvablePotionEffect(typeId, duration, amplifier, ambient, particles, showIcon);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected List<ResolvablePotionEffect> parseResolvablePotionEffects(@NotNull List<Map<?, ?>> potionEffectsRaw) {
        List<ResolvablePotionEffect> potionEffects = new ArrayList<>();
        for (var rawPotionEffect : potionEffectsRaw) {
            @SuppressWarnings("unchecked")
            Map<String, Object> potionEffectMap = (Map<String, Object>) rawPotionEffect;
            ResolvablePotionEffect effect = this.parseResolvablePotionEffect(potionEffectMap);
            if (effect != null) potionEffects.add(effect);
        }
        return potionEffects;
    }
}