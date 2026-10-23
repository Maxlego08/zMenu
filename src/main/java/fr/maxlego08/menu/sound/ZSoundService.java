package fr.maxlego08.menu.sound;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.sound.SoundService;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZSoundService implements SoundService {

    @Override
    public @NotNull SoundOption create(@NotNull String sound, @NotNull String category, float pitch, float volume) {
        XSound xSound = XSound.of(sound).orElse(null);
        return new ZSoundOption(xSound, category, sound, pitch, volume, xSound == null);
    }

    @Override
    public @NotNull SoundOption createRandomPitch(@NotNull String sound, @NotNull String category, float minPitch, float maxPitch, float volume) {
        XSound xSound = XSound.of(sound).orElse(null);
        return new RandomPitchSoundOption(xSound, category, sound, minPitch, maxPitch, volume, xSound == null);
    }

    @Override
    public @Nullable SoundOption load(@NotNull YamlConfiguration configuration, @NotNull String path) {
        String sound = configuration.getString(path + ".sound");
        if (sound == null || sound.isEmpty()) return null;

        float volume = (float) configuration.getDouble(path + ".volume", 1);
        String category = configuration.getString(path + ".sound-category", configuration.getString(path + ".category", XSound.Category.MASTER.name()));

        List<Double> pitches = configuration.getDoubleList(path + ".pitch");
        if (pitches.size() >= 2) {
            return this.createRandomPitch(sound, category, pitches.get(0).floatValue(), pitches.get(1).floatValue(), volume);
        }

        return this.create(sound, category, (float) configuration.getDouble(path + ".pitch", 1), volume);
    }
}
