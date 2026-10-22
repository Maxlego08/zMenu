package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public sealed interface PaperResolvableConsumeEffect extends Resolvable<ConsumeEffect> permits PaperResolvableConsumeEffect.PlaySound, PaperResolvableConsumeEffect.ApplyEffects, PaperResolvableConsumeEffect.TeleportRandomly, PaperResolvableConsumeEffect.ClearAllEffects, PaperResolvableConsumeEffect.RemoveEffects {

    static @Nullable PaperResolvableConsumeEffect of(@NotNull ConsumeEffect effect) {
        switch (effect) {
            case ConsumeEffect.PlaySound playSound -> {
                NamespacedKey sound = NamespacedKey.fromString(playSound.sound().asString());
                return sound == null ? null : new PlaySound(ResolvableNamespacedKey.of(sound));
            }
            case ConsumeEffect.ApplyStatusEffects applyStatusEffects -> {
                if (applyStatusEffects.effects().isEmpty()) return null;
                List<ResolvablePotionEffect> potionEffects = new ArrayList<>();
                for (PotionEffect potionEffect : applyStatusEffects.effects()) {
                    int amplifier = potionEffect.getAmplifier();
                    if (amplifier != 0 && amplifier != 1) return null;
                    potionEffects.add(new ResolvablePotionEffect(
                            ResolvableString.of(potionEffect.getType().getKey().asString()),
                            ResolvableInt.of(potionEffect.getDuration()),
                            ResolvableByte.of((byte) amplifier),
                            ResolvableBoolean.of(potionEffect.isAmbient()),
                            ResolvableBoolean.of(potionEffect.hasParticles()),
                            ResolvableBoolean.of(potionEffect.hasIcon())
                    ));
                }
                return new ApplyEffects(potionEffects, ResolvableFloat.of(applyStatusEffects.probability()));
            }
            case ConsumeEffect.TeleportRandomly teleportRandomly -> {
                return new TeleportRandomly(ResolvableFloat.of(teleportRandomly.diameter()));
            }
            case ConsumeEffect.ClearAllStatusEffects ignored -> {
                return new ClearAllEffects();
            }
            case ConsumeEffect.RemoveStatusEffects removeStatusEffects -> {
                RegistryKeySet<PotionEffectType> effectTypes = removeStatusEffects.removeEffects();
                if (effectTypes instanceof Tag<PotionEffectType> || effectTypes.values().isEmpty()) return null;
                List<String> keys = new ArrayList<>();
                for (TypedKey<PotionEffectType> key : effectTypes.values()) {
                    keys.add(key.key().asString());
                }
                return new RemoveEffects(ResolvableRegistryKeySet.typedKeySet(RegistryKey.MOB_EFFECT, keys));
            }
            default -> {
            }
        }
        return null;
    }

    record PlaySound(@NotNull ResolvableNamespacedKey sound) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            NamespacedKey resolvedSound = this.sound.resolve(context);
            if (resolvedSound == null) return null;
            return ConsumeEffect.playSoundConsumeEffect(resolvedSound);
        }

        /**
         * Writes {@code type: play_sound} and {@code sound}.
         */
        @Override
        public @NotNull Object serialize() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", "play_sound");
            map.put("sound", this.sound.serialize());
            return map;
        }
    }

    record ApplyEffects(@NotNull List<ResolvablePotionEffect> potionEffects, ResolvableFloat probability) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            Float resolveProb = Resolvable.resolve(context, this.probability);
            if (resolveProb == null) return null;
            List<PotionEffect> effects = new ArrayList<>();
            for (ResolvablePotionEffect pe : this.potionEffects) {
                PotionEffect resolved = pe.resolve(context);
                if (resolved != null) {
                    effects.add(resolved);
                }
            }
            if (effects.isEmpty()) return null;
            return ConsumeEffect.applyStatusEffects(effects, resolveProb);
        }

        /**
         * Writes {@code type: apply_effects}, {@code potion-effects} as effect maps and, when set, {@code probability}.
         */
        @Override
        public @NotNull Object serialize() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", "apply_effects");
            map.put("potion-effects", Resolvable.serializeList(this.potionEffects));
            if (this.probability != null) map.put("probability", this.probability.serialize());
            return map;
        }
    }

    record TeleportRandomly(ResolvableFloat diameter) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            Float resolved = Resolvable.resolve(context, this.diameter);
            if (resolved == null) return null;
            return ConsumeEffect.teleportRandomlyEffect(resolved);
        }

        /**
         * Writes {@code type: teleport_randomly} and, when set, {@code diameter}.
         */
        @Override
        public @NotNull Object serialize() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", "teleport_randomly");
            if (this.diameter != null) map.put("diameter", this.diameter.serialize());
            return map;
        }
    }

    record ClearAllEffects() implements PaperResolvableConsumeEffect {
        @Override
        public @NotNull ConsumeEffect resolve(@NotNull BuildContext context) {
            return ConsumeEffect.clearAllStatusEffects();
        }

        /**
         * Writes {@code type: clear_all_effects}.
         */
        @Override
        public @NotNull Object serialize() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", "clear_all_effects");
            return map;
        }
    }

    record RemoveEffects(@NotNull Resolvable<RegistryKeySet<PotionEffectType>> effectTypes) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            RegistryKeySet<PotionEffectType> keys = this.effectTypes.resolve(context);
            if (keys == null) return null;
            return ConsumeEffect.removeEffects(keys);
        }

        /**
         * Writes {@code type: remove_effects} and {@code effects} as a list of effect keys.
         */
        @Override
        public @NotNull Object serialize() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", "remove_effects");
            map.put("effects", this.effectTypes.serialize());
            return map;
        }
    }
}
