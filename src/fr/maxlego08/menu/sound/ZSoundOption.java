package fr.maxlego08.menu.sound;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.entity.Entity;

public class ZSoundOption implements SoundOption {

    private final XSound sound;
    private final float pitch;
    private final float volume;

    public ZSoundOption(XSound sound, float pitch, float volume) {
        super();
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    /**
     * @return the sound
     */
    public XSound getSound() {
        return this.sound;
    }

    /**
     * @return the pitch
     */
    public float getPitch() {
        return this.pitch;
    }

    /**
     * @return the volume
     */
    public float getVolume() {
        return this.volume;
    }

    @Override
    public void play(Entity entity) {
        if (this.sound != null) {
            this.sound.play(entity, this.volume, this.pitch);
        }
    }

}
