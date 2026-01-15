package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.zcore.utils.itemstack.*;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConsumableItemComponentLoader extends ItemComponentLoader {

    public ConsumableItemComponentLoader() {
        super("consumable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        double consumeSeconds = componentSection.getDouble("consume_seconds", 1.6f);
        ConsumableComponent.Animation animation = parseAnimation(configuration.getString("animation", "EAT"));
        Sound consumeSound = parseSound(configuration.getString("consume_sound", "ENTITY_GENERIC_EAT"));
        boolean hasConsumeParticles = componentSection.getBoolean("has_consume_particles", true);
        List<ConsumableEffect> effects = parseEffects(componentSection.getMapList("on_consume_effects"));

        return new fr.maxlego08.menu.itemstack.components.ConsumableComponent(
                (float) consumeSeconds, animation, consumeSound, hasConsumeParticles, effects
        );
    }

    private ConsumableComponent.Animation parseAnimation(String animationString) {
        try {
            return ConsumableComponent.Animation.valueOf(animationString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ConsumableComponent.Animation.EAT;
        }
    }

    private Sound parseSound(String soundString) {
        try {
            NamespacedKey key = NamespacedKey.fromString(soundString.toLowerCase());
            if (key == null) throw new IllegalArgumentException();
            return Registry.SOUNDS.getOrThrow(key);
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_GENERIC_EAT;
        }
    }

    private @Nullable NamespacedKey parseNamespacedKey(String value) {
        return NamespacedKey.fromString(value.toLowerCase());
    }

    private List<ConsumableEffect> parseEffects(List<Map<?, ?>> onConsumeEffectsRaw) {
        List<ConsumableEffect> effects = new ArrayList<>();
        for (var rawEffect : onConsumeEffectsRaw) {
            @SuppressWarnings("unchecked")
            Map<String, Object> effectMap = (Map<String, Object>) rawEffect;
            parseEffect(effectMap).ifPresent(effects::add);
        }
        return effects;
    }

    private Optional<ConsumableEffect> parseEffect(Map<String, Object> effectMap) {
        try {
            ConsumableTypes type = parseConsumableType((String) effectMap.get("type"));
            if (type == null) return Optional.empty();

            return switch (type) {
                case PLAY_SOUND -> parsePlaySound(effectMap);
                case APPLY_EFFECTS -> parseApplyEffects(effectMap);
                case TELEPORT_RANDOMLY -> parseTeleportRandomly(effectMap);
                case CLEAR_ALL_EFFECTS -> Optional.of(new ZConsumableClearEffects());
                case REMOVE_EFFECTS -> parseRemoveEffects(effectMap);
            };
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private @Nullable ConsumableTypes parseConsumableType(String type) {
        try {
            return ConsumableTypes.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }

    private Optional<ConsumableEffect> parsePlaySound(Map<String, Object> effectMap) {
        String soundString = (String) effectMap.get("sound");
        if (soundString == null) return Optional.empty();

        try {
            NamespacedKey key = parseNamespacedKey(soundString);
            if (key == null) return Optional.empty();
            Sound sound = Registry.SOUNDS.getOrThrow(key);
            return Optional.of(new ZConsumablePlaySound(sound));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<ConsumableEffect> parseApplyEffects(Map<String, Object> effectMap) {
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> potionEffectsRaw = (List<Map<?, ?>>) effectMap.get("potion_effects");
        if (potionEffectsRaw == null) return Optional.empty();

        List<PotionEffect> potionEffects = parsePotionEffects(potionEffectsRaw);
        double probability = (double) effectMap.getOrDefault("probability", 1.0);
        return Optional.of(new ZConsumableApplyEffects((float) probability, potionEffects));
    }

    private List<PotionEffect> parsePotionEffects(List<Map<?, ?>> potionEffectsRaw) {
        List<PotionEffect> potionEffects = new ArrayList<>();
        for (var rawPotionEffect : potionEffectsRaw) {
            @SuppressWarnings("unchecked")
            Map<String, Object> potionEffectMap = (Map<String, Object>) rawPotionEffect;
            parsePotionEffect(potionEffectMap).ifPresent(potionEffects::add);
        }
        return potionEffects;
    }

    private Optional<PotionEffect> parsePotionEffect(Map<String, Object> potionEffectMap) {
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

    private Optional<ConsumableEffect> parseTeleportRandomly(Map<String, Object> effectMap) {
        double diameter = (double) effectMap.getOrDefault("diameter", 16.0);
        return Optional.of(new ZConsumableTeleportRandomly((float) diameter));
    }

    private Optional<ConsumableEffect> parseRemoveEffects(Map<String, Object> effectMap) {
        Object effectObj = effectMap.get("effects");
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

        return potionEffectTypes.isEmpty()
                ? Optional.empty()
                : Optional.of(new ZConsumableRemoveEffect(potionEffectTypes));
    }

    private Optional<PotionEffectType> parsePotionEffectType(String effectString) {
        try {
            NamespacedKey key = parseNamespacedKey(effectString);
            if (key == null) return Optional.empty();
            return Optional.of(Registry.EFFECT.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private enum ConsumableTypes {
        APPLY_EFFECTS,
        REMOVE_EFFECTS,
        CLEAR_ALL_EFFECTS,
        TELEPORT_RANDOMLY,
        PLAY_SOUND
    }
}