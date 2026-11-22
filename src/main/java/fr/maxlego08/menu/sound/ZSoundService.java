package fr.maxlego08.menu.sound;

import com.cryptomorin.xseries.XSound;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.sound.SoundService;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

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
    public @Nullable SoundOption load(@NotNull YamlDocument configuration, @NotNull String path) {
        String sound = configuration.getString(path + ".sound");
        if (sound == null || sound.isEmpty()) return null;

        float volume = (float) (double) configuration.getDouble(path + ".volume", 1.0);
        String category = configuration.getString(path + ".sound-category", configuration.getString(path + ".category", XSound.Category.MASTER.name()));

        List<Double> pitches = configuration.getDoubleList(path + ".pitch");
        if (pitches.size() >= 2) {
            return this.createRandomPitch(sound, category, pitches.get(0).floatValue(), pitches.get(1).floatValue(), volume);
        }

        return this.create(sound, category, (float) (double) configuration.getDouble(path + ".pitch", 1.0), volume);
    }

    @Override
    @Deprecated
    public SoundOption load(YamlConfiguration configuration, String path) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(configurationDocument, path);
    }
}
