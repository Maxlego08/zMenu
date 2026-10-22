package fr.maxlego08.menu.sound;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A sound whose pitch is picked inside a range every time it is played.
 *
 * <p>The same sound repeated at the same pitch is what makes an animation grate; a little variation
 * is what makes it read as musical.</p>
 */
public class RandomPitchSoundOption implements SoundOption {

    private final XSound sound;
    private final String category;
    private final String soundAsString;
    private final float minPitch;
    private final float maxPitch;
    private final float volume;
    private final boolean isCustom;

    public RandomPitchSoundOption(@Nullable XSound sound, String category, @Nullable String soundAsString,
                                  float minPitch, float maxPitch, float volume, boolean isCustom) {
        this.sound = sound;
        this.category = category;
        this.soundAsString = soundAsString;
        this.minPitch = Math.min(minPitch, maxPitch);
        this.maxPitch = Math.max(minPitch, maxPitch);
        this.volume = volume;
        this.isCustom = isCustom;
    }

    @Override
    public XSound getSound() {
        return this.sound;
    }

    @Override
    public float getPitch() {
        return this.minPitch >= this.maxPitch
                ? this.minPitch
                : (float) ThreadLocalRandom.current().nextDouble(this.minPitch, this.maxPitch);
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public boolean isCustom() {
        return this.isCustom;
    }

    @Override
    public @Nullable String getSoundName() {
        if (this.soundAsString != null) return this.soundAsString;
        return this.sound == null ? null : this.sound.name();
    }

    @Override
    public @NonNull String getCategoryName() {
        return this.category;
    }

    @Override
    public void serialize(@NonNull Map<String, Object> map) {
        String soundName = this.getSoundName();
        if (soundName != null) map.put("sound", soundName);
        map.put("pitch", List.of(this.minPitch, this.maxPitch));
        if (this.volume != 1f) map.put("volume", this.volume);
        if (!XSound.Category.MASTER.name().equalsIgnoreCase(this.category)) map.put("sound-category", this.category);
    }

    @Override
    public void play(@NonNull Entity entity) {
        new ZSoundOption(this.sound, this.category, this.soundAsString, this.getPitch(), this.volume, this.isCustom).play(entity);
    }
}
