package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.InstrumentComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZMusicInstrument;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotInstrumentItemComponentLoader extends ItemComponentLoader {

    public SpigotInstrumentItemComponentLoader(){
        super("instrument");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) {
            path = normalizePath(path);
            String instrumentName = configuration.getString(path, "");
            if (!instrumentName.isBlank()) {
                NamespacedKey key = NamespacedKey.fromString(instrumentName);
                if (key != null) {
                    try {
                        MusicInstrument instrument = Registry.INSTRUMENT.getOrThrow(key);
                        return new InstrumentComponent(instrument);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
            return null;
        }
        String description = componentSection.getString("description", "");
        float useDuration = (float) componentSection.getDouble("use_duration", 0);
        float range = (float) componentSection.getDouble("range", 0);
        String soundKey = configuration.getString("sound_event","");
        NamespacedKey key = NamespacedKey.fromString(soundKey);
        if (key != null) {
            try {
                MusicInstrument instrument = Registry.INSTRUMENT.getOrThrow(key);
                Sound soundEvent = instrument.getSoundEvent();
                return new InstrumentComponent(new ZMusicInstrument(key, useDuration, range,description, soundEvent));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
