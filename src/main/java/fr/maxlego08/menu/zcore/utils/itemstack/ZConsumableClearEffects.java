package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.inventory.meta.components.consumable.effects.ConsumableClearEffects;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public record ZConsumableClearEffects() implements ConsumableClearEffects {
    @Override
    public @NonNull Map<String, Object> serialize() {
        return Map.of();
    }
}
