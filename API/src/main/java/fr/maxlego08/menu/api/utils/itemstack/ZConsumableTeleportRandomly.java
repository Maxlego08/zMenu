package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.inventory.meta.components.consumable.effects.ConsumableTeleportRandomly;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ZConsumableTeleportRandomly implements ConsumableTeleportRandomly {
    private float diameter;

    public ZConsumableTeleportRandomly(float diameter) {
        this.diameter = diameter;
    }

    @Override
    public float getDiameter() {
        return this.diameter;
    }

    @Override
    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        return Map.of("diameter", this.diameter);
    }
}
