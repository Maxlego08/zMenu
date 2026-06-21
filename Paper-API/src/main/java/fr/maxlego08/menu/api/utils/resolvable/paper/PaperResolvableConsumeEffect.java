package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public sealed interface PaperResolvableConsumeEffect {

    @Nullable ConsumeEffect resolve(@NotNull BuildContext context);

    record PlaySound(@NotNull Resolvable<String> sound) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            String resolvedSound = this.sound.resolve(context);
            if (resolvedSound == null) return null;
            return ConsumeEffect.playSoundConsumeEffect(Key.key(resolvedSound));
        }
    }

    record ApplyEffects(@NotNull List<ResolvablePotionEffect> potionEffects, float probability) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            List<PotionEffect> effects = new ArrayList<>();
            for (ResolvablePotionEffect pe : this.potionEffects) {
                PotionEffect resolved = pe.resolve(context);
                if (resolved != null) {
                    effects.add(resolved);
                }
            }
            if (effects.isEmpty()) return null;
            return ConsumeEffect.applyStatusEffects(effects, this.probability);
        }
    }

    record TeleportRandomly(float diameter) implements PaperResolvableConsumeEffect {
        @Override
        public @NotNull ConsumeEffect resolve(@NotNull BuildContext context) {
            return ConsumeEffect.teleportRandomlyEffect(this.diameter);
        }
    }

    record ClearAllEffects() implements PaperResolvableConsumeEffect {
        @Override
        public @NotNull ConsumeEffect resolve(@NotNull BuildContext context) {
            return ConsumeEffect.clearAllStatusEffects();
        }
    }

    record RemoveEffects(@NotNull List<Resolvable<String>> effectTypes) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            List<TypedKey<PotionEffectType>> keys = new ArrayList<>();
            for (Resolvable<String> resolvableType : this.effectTypes) {
                String resolved = resolvableType.resolve(context);
                if (resolved == null) continue;
                NamespacedKey key = NamespacedKey.fromString(resolved);
                if (key == null) continue;
                PotionEffectType pet = Registry.EFFECT.getOrThrow(key);
                keys.add(TypedKey.create(RegistryKey.MOB_EFFECT, pet.key()));
            }
            if (keys.isEmpty()) return null;
            RegistryKeySet<PotionEffectType> registryKeySet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, keys);
            return ConsumeEffect.removeEffects(registryKeySet);
        }
    }
}
