package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public sealed interface PaperResolvableConsumeEffect extends Resolvable<ConsumeEffect> permits PaperResolvableConsumeEffect.PlaySound, PaperResolvableConsumeEffect.ApplyEffects, PaperResolvableConsumeEffect.TeleportRandomly, PaperResolvableConsumeEffect.ClearAllEffects, PaperResolvableConsumeEffect.RemoveEffects {

    record PlaySound(@NotNull ResolvableNamespacedKey sound) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            NamespacedKey resolvedSound = this.sound.resolve(context);
            if (resolvedSound == null) return null;
            return ConsumeEffect.playSoundConsumeEffect(resolvedSound);
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
    }

    record TeleportRandomly(ResolvableFloat diameter) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            Float resolved = Resolvable.resolve(context, this.diameter);
            if (resolved == null) return null;
            return ConsumeEffect.teleportRandomlyEffect(resolved);
        }
    }

    record ClearAllEffects() implements PaperResolvableConsumeEffect {
        @Override
        public @NotNull ConsumeEffect resolve(@NotNull BuildContext context) {
            return ConsumeEffect.clearAllStatusEffects();
        }
    }

    record RemoveEffects(@NotNull Resolvable<RegistryKeySet<PotionEffectType>> effectTypes) implements PaperResolvableConsumeEffect {
        @Override
        public @Nullable ConsumeEffect resolve(@NotNull BuildContext context) {
            RegistryKeySet<PotionEffectType> keys = this.effectTypes.resolve(context);
            if (keys == null) return null;
            return ConsumeEffect.removeEffects(keys);
        }
    }
}
