package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.inventory.meta.components.consumable.effects.ConsumableApplyEffects;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ZConsumableApplyEffects implements ConsumableApplyEffects {
    private final List<PotionEffect> effects;
    private float probability;

    public ZConsumableApplyEffects(float probability, @NonNull List<PotionEffect> effects) {
        this.effects = effects;
        this.probability = probability;
    }

    @Override
    public @NonNull List<PotionEffect> getEffects() {
        return Collections.unmodifiableList(this.effects);
    }

    @Override
    public void setEffects(@NonNull List<PotionEffect> effects) {
        this.effects.clear();
        this.effects.addAll(effects);
    }

    @Override
    public @NonNull PotionEffect addEffect(@NonNull PotionEffect effect) {
        this.effects.add(effect);
        return effect;
    }

    @Override
    public float getProbability() {
        return this.probability;
    }

    @Override
    public void setProbability(float probability) {
        this.probability = probability;
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        return Map.of();
    }
}
