package fr.maxlego08.menu.api.sound;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

}
