package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.JukeboxPlayableComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.ZJukeboxPlayableComponent;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class JukeboxPlayableItemComponentLoader extends ItemComponentLoader {

    public JukeboxPlayableItemComponentLoader(){
        super("jukebox_playable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String soundStr = configuration.getString(path);
        if (soundStr == null || soundStr.isEmpty()) return null;
        NamespacedKey key = NamespacedKey.fromString(soundStr);
        if (key != null) {
            try {
                JukeboxSong song = Registry.JUKEBOX_SONG.getOrThrow(key);
                return new JukeboxPlayableComponent(new ZJukeboxPlayableComponent(song, key));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
