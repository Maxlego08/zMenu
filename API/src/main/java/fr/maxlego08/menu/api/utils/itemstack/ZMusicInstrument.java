package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class ZMusicInstrument extends MusicInstrument {
    private final NamespacedKey key;
    private final float duration;
    private final float range;
    private final String description;
    private final Sound soundEvent;

    public ZMusicInstrument(@NotNull NamespacedKey key, float duration, float range, @NotNull String description, @NotNull Sound soundEvent) {
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
    public @NotNull String getDescription() {
        return this.description;
    }

    @Override
    public @NotNull Sound getSoundEvent() {
        return this.soundEvent;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public @NotNull NamespacedKey getKeyOrThrow() {
        return this.key;
    }

    @Override
    public @Nullable NamespacedKey getKeyOrNull() {
        return this.key;
    }

    @Override
    public boolean isRegistered() {
        return false;
    }
}
