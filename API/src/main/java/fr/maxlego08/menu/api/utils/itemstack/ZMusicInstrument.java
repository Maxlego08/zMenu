package fr.maxlego08.menu.api.utils.itemstack;

import net.kyori.adventure.text.Component;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class ZMusicInstrument extends MusicInstrument {
    private final NamespacedKey key;
    private final float duration;
    private final float range;
    private final Component description;
    private final Sound soundEvent;

    public ZMusicInstrument(@NotNull NamespacedKey key, float duration, float range, @NotNull Component description, @NotNull Sound soundEvent) {
        this.key = key;
        this.duration = duration;
        this.range = range;
        this.description = description;
        this.soundEvent = soundEvent;
    }

    @Override
    public float getDuration() {
        return this.duration;
    }

    @Override
    public float getRange() {
        return this.range;
    }

    @Override
    public Component description() {
        return this.description;
    }

    @Override
    public @Nullable Sound getSound() {
        return this.soundEvent;
    }

    @Override
    public @Nullable NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public String translationKey() {
        return this.key.toString();
    }
}
