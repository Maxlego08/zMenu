package fr.maxlego08.menu.sound;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ZSoundOption implements SoundOption {

    private final XSound sound;
    private final XSound.Category category;
    private final String soundAsString;
    private final float pitch;
    private final float volume;
    private final boolean isCustom;

    public ZSoundOption(XSound sound, String categoryName, String soundAsString, float pitch, float volume, boolean isCustom) {
        super();
        XSound.Category localCategory;
        this.sound = sound;
        try {
            localCategory = XSound.Category.valueOf(categoryName.toUpperCase());
        } catch (Exception e) {
            localCategory = XSound.Category.MASTER;
        }
        this.category = localCategory;
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

        if (this.soundAsString != null && this.isCustom()) {
            Location location = entity.getLocation();
            if (entity instanceof Player player) {
                SoundCategory soundCategory = SoundCategory.valueOf(this.category.name());
                player.playSound(location, soundAsString, soundCategory, this.volume, this.pitch);
            } else location.getWorld().playSound(location, soundAsString, this.volume, this.pitch);
        }

        if (this.sound != null) {
            if (entity instanceof Player) {
                this.sound.record()
                        .withVolume(volume)
                        .withPitch(pitch)
                        .inCategory(category)
                        .soundPlayer()
                        .forPlayers((Player) entity)
                        .play();
            } else {
                this.sound.play(entity, this.volume, this.pitch);
            }
        }
    }

}
