package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ZKineticWeaponCondition implements KineticWeaponComponent.Condition {
    private int maxDurationTicks = 0;
    private float minSpeed = 0.0f;
    private float minRelativeSpeed = 0.0f;

    public ZKineticWeaponCondition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
        this.maxDurationTicks = maxDurationTicks;
        this.minSpeed = minSpeed;
        this.minRelativeSpeed = minRelativeSpeed;
    }

    public ZKineticWeaponCondition(@NotNull Map<String, Object> map) {
        this.maxDurationTicks = (int) map.getOrDefault("max_duration_ticks", 0);
        this.minSpeed = ((Number) map.getOrDefault("min_speed", 0.0f)).floatValue();
        this.minRelativeSpeed = ((Number) map.getOrDefault("min_relative_speed", 0.0f)).floatValue();
    }

    public ZKineticWeaponCondition() {
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
