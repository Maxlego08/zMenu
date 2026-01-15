package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.Sound;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumablePlaySound;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class ZConsumablePlaySound implements ConsumablePlaySound {
    private @Nullable Sound sound;

    public ZConsumablePlaySound(@Nullable Sound sound) {
        this.sound = sound;
    }

    @Override
    public @Nullable Sound getSound() {
        return this.sound;
    }

    @Override
    public void setSound(@Nullable Sound sound) {
        this.sound = sound;
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        return Map.of();
    }
}
