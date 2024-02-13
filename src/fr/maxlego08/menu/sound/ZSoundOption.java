package fr.maxlego08.menu.sound;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ZSoundOption implements SoundOption {

    private final XSound sound;
    private final String soundAsString;
    private final float pitch;
    private final float volume;
    private final boolean isCustom;

    public ZSoundOption(XSound sound, String soundAsString, float pitch, float volume, boolean isCustom) {
        super();
        this.sound = sound;
        this.soundAsString = soundAsString;
        this.pitch = pitch;
        this.volume = volume;
        this.isCustom = isCustom;
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
    public boolean isCustom() {
        return this.isCustom;
    }

    @Override
    public void play(Entity entity) {
        if (this.soundAsString != null && this.isCustom()){
            Location location = entity.getLocation();
            location.getWorld().playSound(location, soundAsString, this.volume, this.pitch);
        }
        if (this.sound != null) {
            this.sound.play(entity, this.volume, this.pitch);
        }
    }

}
