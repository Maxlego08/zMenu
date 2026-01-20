package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.itemstack.*;
import fr.maxlego08.menu.common.enums.ConsumeEffectType;
import fr.maxlego08.menu.loader.components.AbstractEffectItemComponentLoader;
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

public class SpigotConsumableItemComponentLoader extends AbstractEffectItemComponentLoader {

    public SpigotConsumableItemComponentLoader() {
        super("consumable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        double consumeSeconds = componentSection.getDouble("consume_seconds", 1.6f);
        ConsumableComponent.Animation animation = parseAnimation(configuration.getString("animation", "EAT"));
        Sound consumeSound = parseSound(configuration.getString("consume_sound", "ENTITY_GENERIC_EAT")).orElse(Sound.ENTITY_GENERIC_EAT);
        boolean hasConsumeParticles = componentSection.getBoolean("has_consume_particles", true);
        List<ConsumableEffect> effects = parseEffects(componentSection.getMapList("on_consume_effects"));

        return new fr.maxlego08.menu.api.itemstack.components.ConsumableComponent(
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
            ConsumeEffectType type = parseConsumableType((String) effectMap.get("type"));
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
        List<Map<?, ?>> potionEffectsRaw = (List<Map<?, ?>>) effectMap.get("effects");
        if (potionEffectsRaw == null) return Optional.empty();

        List<PotionEffect> potionEffects = parsePotionEffects(potionEffectsRaw);
        float probability = parseFloat(effectMap, "probability", 1.0f);
        return Optional.of(new ZConsumableApplyEffects(probability, potionEffects));
    }

    private Optional<ConsumableEffect> parseTeleportRandomly(Map<String, Object> effectMap) {
        float diameter = parseFloat(effectMap, "diameter", 16.0f);
        return Optional.of(new ZConsumableTeleportRandomly(diameter));
    }

    private Optional<ConsumableEffect> parseRemoveEffects(Map<String, Object> effectMap) {
        List<PotionEffectType> potionEffectTypes = parsePotionEffectTypes(effectMap.get("effects"));
        return potionEffectTypes.isEmpty()
                ? Optional.empty()
                : Optional.of(new ZConsumableRemoveEffect(potionEffectTypes));
    }
}