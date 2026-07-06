package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.*;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableConsumeEffect;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableRegistryKeySet;
import fr.maxlego08.menu.common.enums.ConsumeEffectType;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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

            ResolvableString typeId = ResolvableString.auto(idString);
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

    protected List<PaperResolvableConsumeEffect> parseEffects(List<Map<?, ?>> onConsumeEffectsRaw) {
        List<PaperResolvableConsumeEffect> effects = new ArrayList<>();
        for (var effectRaw : onConsumeEffectsRaw) {
            Map<String, Object> effectMap = (Map<String, Object>) effectRaw;
            PaperResolvableConsumeEffect effect = this.parseEffect(effectMap);
            if (effect != null) {
                effects.add(effect);
            }
        }
        return effects;
    }

    @SuppressWarnings("unchecked")
    protected PaperResolvableConsumeEffect parseEffect(Map<String, Object> effectMap) {
        String typeString = (String) effectMap.get("type");
        if (typeString == null) return null;

        ConsumeEffectType type = this.parseConsumableType(typeString);
        if (type == null) return null;

        return switch (type) {
            case PLAY_SOUND -> {
                Object soundObj = effectMap.get("sound");
                if (!(soundObj instanceof String soundString)) yield null;
                yield new PaperResolvableConsumeEffect.PlaySound(ResolvableNamespacedKey.auto(soundString));
            }
            case APPLY_EFFECTS -> {
                List<Map<?, ?>> rawPotionEffects = (List<Map<?, ?>>) effectMap.get("potion-effects");
                if (rawPotionEffects == null || rawPotionEffects.isEmpty()) yield null;

                List<ResolvablePotionEffect> potionEffects = new ArrayList<>();
                for (Map<?, ?> raw : rawPotionEffects) {
                    Map<String, Object> pem = (Map<String, Object>) raw;
                    ResolvablePotionEffect pe = this.parsePotionEffectEntry(pem);
                    if (pe != null) potionEffects.add(pe);
                }
                if (potionEffects.isEmpty()) yield null;

                ResolvableFloat probability = ResolvableFloat.of(effectMap, "probability", 1.0f);
                yield new PaperResolvableConsumeEffect.ApplyEffects(potionEffects, probability);
            }
            case TELEPORT_RANDOMLY -> {
                ResolvableFloat diameter = ResolvableFloat.of(effectMap, "diameter", 16.0f);
                yield new PaperResolvableConsumeEffect.TeleportRandomly(diameter);
            }
            case CLEAR_ALL_EFFECTS -> new PaperResolvableConsumeEffect.ClearAllEffects();
            case REMOVE_EFFECTS -> {
                Object effectsObj = effectMap.get("effects");
                if (effectsObj == null) yield null;
                yield new PaperResolvableConsumeEffect.RemoveEffects(
                        ResolvableRegistryKeySet.typedKeySet(RegistryKey.MOB_EFFECT, effectsObj)
                );
            }
        };
    }

    private @Nullable ResolvablePotionEffect parsePotionEffectEntry(Map<String, Object> map) {
        Object idObj = map.get("id");
        if (!(idObj instanceof String idString)) return null;

        ResolvableInt duration = asRawResolvableInt(map, "duration", 1);
        ResolvableBoolean amplified = asRawResolvableBoolean(map, "amplified", false);

        ResolvableByte amplifier;
        if (amplified.isDynamic()) {
            amplifier = ResolvableByte.of(amplified.getExpression());
        } else {
            amplifier = ResolvableByte.of((byte) (Boolean.TRUE.equals(amplified.getResolvedValue()) ? 1 : 0));
        }

        ResolvableBoolean ambient = asRawResolvableBoolean(map, "ambient", false);
        ResolvableBoolean particles = asRawResolvableBoolean(map, "show_particles", true);
        ResolvableBoolean showIcon = asRawResolvableBoolean(map, "show_icon", true);

        return new ResolvablePotionEffect(
                asResolvableString(idString),
                duration,
                amplifier,
                ambient,
                particles,
                showIcon
        );
    }

    private static @NotNull Resolvable<String> asResolvableString(@NotNull String value) {
        return value.contains("%") ? ResolvableString.ofExpression(value) : ResolvableString.of(value);
    }

    private static @NotNull ResolvableInt asRawResolvableInt(@NotNull Map<String, Object> map, @NotNull String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number number) return ResolvableInt.of(number.intValue());
        if (value instanceof String expr) return ResolvableInt.of(expr);
        return ResolvableInt.of(defaultValue);
    }

    private static @NotNull ResolvableBoolean asRawResolvableBoolean(@NotNull Map<String, Object> map, @NotNull String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean bool) return ResolvableBoolean.of(bool);
        if (value instanceof String expr) return ResolvableBoolean.of(expr);
        return ResolvableBoolean.of(defaultValue);
    }

}