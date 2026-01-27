package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ZJukeboxPlayableComponent implements JukeboxPlayableComponent {
    private @NotNull JukeboxSong song;
    private @NotNull NamespacedKey songKey;

    public ZJukeboxPlayableComponent(@NotNull JukeboxSong song, @NotNull NamespacedKey songKey) {
        this.song = song;
        this.songKey = songKey;
    }

    @Override
    public @NonNull JukeboxSong getSong() {
        return this.song;
    }

    @Override
    public @NonNull NamespacedKey getSongKey() {
        return this.songKey;
    }

    @Override
    public void setSong(@NotNull JukeboxSong song) {
        this.song = song;
    }

    @Override
    public void setSongKey(@NotNull NamespacedKey song) {
        this.songKey = song;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("songKey", this.songKey, "song", this.song);
    }
}
