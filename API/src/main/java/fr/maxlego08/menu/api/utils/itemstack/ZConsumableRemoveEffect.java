package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.inventory.meta.components.consumable.effects.ConsumableRemoveEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ZConsumableRemoveEffect implements ConsumableRemoveEffect {
    private final List<PotionEffectType> effectTypes;

    public ZConsumableRemoveEffect(@NonNull List<PotionEffectType> effectTypes) {
        this.effectTypes = effectTypes;
    }

    @Override
    public @NonNull List<PotionEffectType> getEffectTypes() {
        return Collections.unmodifiableList(this.effectTypes);
    }

    @Override
    public void setEffectTypes(@NonNull List<PotionEffectType> effects) {
        this.effectTypes.clear();
        this.effectTypes.addAll(effects);
    }

    @Override
    public @NonNull PotionEffectType addEffectType(@NonNull PotionEffectType effect) {
        this.effectTypes.add(effect);
        return effect;
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        return Map.of();
    }
}
