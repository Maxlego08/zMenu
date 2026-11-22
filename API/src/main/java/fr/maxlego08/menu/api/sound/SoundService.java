package fr.maxlego08.menu.api.sound;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.dejvokep.boostedyaml.YamlDocument;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * Creates the sounds zMenu plays, and reads them from configuration files.
 *
 * @see fr.maxlego08.menu.api.MenuPlugin#getSoundService()
 */
public interface SoundService {

    /**
     * Creates a sound. A name Minecraft does not know is played as a custom sound, such as one
     * from a resource pack.
     *
     * @param sound    the name of the sound
     * @param category the sound category, such as {@code MASTER}
     * @param pitch    the pitch
     * @param volume   the volume
     * @return the sound
     */
    @NotNull
    SoundOption create(@NotNull String sound, @NotNull String category, float pitch, float volume);

    /**
     * Creates a sound whose pitch is picked between two values every time it plays.
     *
     * @param sound    the name of the sound
     * @param category the sound category, such as {@code MASTER}
     * @param minPitch the lowest pitch
     * @param maxPitch the highest pitch
     * @param volume   the volume
     * @return the sound
     */
    @NotNull
    SoundOption createRandomPitch(@NotNull String sound, @NotNull String category, float minPitch, float maxPitch, float volume);

    /**
     * Reads a sound written as a section: {@code sound}, {@code volume}, {@code category} (or
     * {@code sound-category}) and {@code pitch}, either a number or a {@code [min, max]} range.
     *
     * @param configuration the configuration
     * @param path          the path of the section, without a trailing dot
     * @return the sound, or null if the section has no {@code sound}
     * @deprecated use {@link #load(YamlDocument, String)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @Nullable
    SoundOption load(@NotNull YamlConfiguration configuration, @NotNull String path);

    /**
     * Reads a sound written as a section of a document.
     * By default it reads a Bukkit copy with the deprecated method.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     *
     * @param configuration the document
     * @param path          the path of the section, without a trailing dot
     * @return the sound, or null if the section has no {@code sound}
     */
    @Nullable
    default SoundOption load(@NotNull YamlDocument configuration, @NotNull String path) {
        return this.load(YamlSections.toBukkit(configuration), path);
    }
}
