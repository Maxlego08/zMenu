package fr.maxlego08.menu.api.sound;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Entity;

public interface SoundOption {

    /**
     * @return sound
     */
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
    void play(Entity entity);

}
