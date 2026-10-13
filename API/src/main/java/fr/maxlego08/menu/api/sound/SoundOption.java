package fr.maxlego08.menu.api.sound;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Allows you to manage a sound
 */
public interface SoundOption {

    /**
     * @return sound
     */
    @Nullable
	XSound getSound();

    /**
     * @return pitch
     */
	float getPitch();

    /**
     * @return volume
     */
	float getVolume();

    /**
     * @param entity to play the sound
     */
    void play(@NotNull Entity entity);

    boolean isCustom();

    /**
     * @return the sound as written in the configuration: the custom sound key for a custom sound,
     * otherwise the name of {@link #getSound()}; null when there is no sound
     */
    @Nullable
    default String getSoundName() {
        XSound sound = this.getSound();
        return sound == null ? null : sound.name();
    }

    /**
     * @return the name of the category the sound plays in
     */
    @NotNull
    default String getCategoryName() {
        return XSound.Category.MASTER.name();
    }

    /**
     * Writes this sound with the keys the sound loaders read: {@code sound}, {@code pitch},
     * {@code volume} and {@code sound-category}. Values equal to their default are left out.
     *
     * @param map The map to write into.
     */
    default void serialize(@NotNull Map<String, Object> map) {
        String soundName = this.getSoundName();
        if (soundName != null) map.put("sound", soundName);
        if (this.getPitch() != 1f) map.put("pitch", this.getPitch());
        if (this.getVolume() != 1f) map.put("volume", this.getVolume());
        if (!XSound.Category.MASTER.name().equals(this.getCategoryName())) map.put("sound-category", this.getCategoryName());
    }

}
