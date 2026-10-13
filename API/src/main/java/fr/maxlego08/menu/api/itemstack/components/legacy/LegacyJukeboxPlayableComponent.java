package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.JukeboxPlayableComponent;
import fr.maxlego08.menu.api.utils.itemstack.ZJukeboxPlayableComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyJukeboxPlayableComponent extends JukeboxPlayableComponent {

    public LegacyJukeboxPlayableComponent(@NotNull ResolvableNamespacedKey songKey) {
        super(songKey);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        NamespacedKey key = this.getSongKey().resolve(context);
        if (key == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        try {
            JukeboxSong song = Registry.JUKEBOX_SONG.getOrThrow(key);
            itemMeta.setJukeboxPlayable(new ZJukeboxPlayableComponent(song, key));
            itemStack.setItemMeta(itemMeta);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
