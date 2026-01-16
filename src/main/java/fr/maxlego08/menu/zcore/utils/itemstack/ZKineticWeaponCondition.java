package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ZKineticWeaponCondition implements KineticWeaponComponent.Condition {
    private int maxDurationTicks;
    private float minSpeed;
    private float minRelativeSpeed;

    public ZKineticWeaponCondition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
        this.maxDurationTicks = maxDurationTicks;
        this.minSpeed = minSpeed;
        this.minRelativeSpeed = minRelativeSpeed;
    }

    @Override
    public int getMaxDurationTicks() {
        return this.maxDurationTicks;
    }

    @Override
    public void setMaxDurationTicks(int ticks) {
        this.maxDurationTicks = ticks;
    }

    @Override
    public float getMinSpeed() {
        return this.minSpeed;
    }

    @Override
    public void setMinSpeed(float speed) {
        this.minSpeed = speed;
    }

    @Override
    public float getMinRelativeSpeed() {
        return this.minRelativeSpeed;
    }

    @Override
    public void setMinRelativeSpeed(float speed) {
        this.minRelativeSpeed = speed;
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        return Map.of("max_duration_ticks", this.maxDurationTicks,
                      "min_speed", this.minSpeed,
                      "min_relative_speed", this.minRelativeSpeed);
    }
}
