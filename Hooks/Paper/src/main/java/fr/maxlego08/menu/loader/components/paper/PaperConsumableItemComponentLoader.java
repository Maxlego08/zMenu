package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.paper.PaperConsumableComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.*;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableConsumeEffect;
import fr.maxlego08.menu.common.enums.ConsumeEffectType;
import fr.maxlego08.menu.loader.components.AbstractEffectItemComponentLoader;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.21.2")
@PaperOnly
public class PaperConsumableItemComponentLoader extends AbstractEffectItemComponentLoader {

    public PaperConsumableItemComponentLoader() {
        super("consumable");
    }


    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableFloat consumeSeconds = this.asResolvableFloat(componentSection, "consume-seconds", 1.6f);
        Resolvable<ItemUseAnimation> animation = this.asResolvableEnum(componentSection, "animation", ItemUseAnimation.class, ItemUseAnimation.EAT);
        ResolvableNamespacedKey consumeSound = this.asResolvableKey(componentSection, "consume-sound", NamespacedKey.fromString("minecraft:entity.generic.eat"));
        ResolvableBoolean hasConsumeParticles = this.asResolvableBoolean(componentSection, "has-consume-particles",true);
        List<PaperResolvableConsumeEffect> effects = this.parseEffects(componentSection.getMapList("on-consume-effects"));

        return effects.isEmpty() ? null : new PaperConsumableComponent(
                consumeSeconds,
                animation,
                consumeSound,
                hasConsumeParticles,
                effects
        );
    }

    private List<PaperResolvableConsumeEffect> parseEffects(List<Map<?, ?>> onConsumeEffectsRaw) {
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
    private PaperResolvableConsumeEffect parseEffect(Map<String, Object> effectMap) {
        String typeString = (String) effectMap.get("type");
        if (typeString == null) return null;

        ConsumeEffectType type = this.parseConsumableType(typeString);
        if (type == null) return null;

        return switch (type) {
            case PLAY_SOUND -> {
                Object soundObj = effectMap.get("sound");
                if (!(soundObj instanceof String soundString)) yield null;
                yield new PaperResolvableConsumeEffect.PlaySound(asResolvableString(soundString));
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

                float probability = this.parseFloat(effectMap, "probability", 1.0f);
                yield new PaperResolvableConsumeEffect.ApplyEffects(potionEffects, probability);
            }
            case TELEPORT_RANDOMLY -> {
                float diameter = this.parseFloat(effectMap, "diameter", 16.0f);
                yield new PaperResolvableConsumeEffect.TeleportRandomly(diameter);
            }
            case CLEAR_ALL_EFFECTS -> new PaperResolvableConsumeEffect.ClearAllEffects();
            case REMOVE_EFFECTS -> {
                List<Resolvable<String>> effectTypes = new ArrayList<>();
                Object effectsObj = effectMap.get("effects");
                if (effectsObj instanceof String single) {
                    effectTypes.add(asResolvableString(single));
                } else if (effectsObj instanceof List<?> list) {
                    for (Object item : list) {
                        if (item instanceof String s) {
                            effectTypes.add(asResolvableString(s));
                        }
                    }
                }
                if (effectTypes.isEmpty()) yield null;
                yield new PaperResolvableConsumeEffect.RemoveEffects(effectTypes);
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



    private static @NotNull Resolvable<String> asResolvableString(@NotNull String value) {
        return value.contains("%") ? ResolvableString.ofExpression(value) : ResolvableString.of(value);
    }
}
