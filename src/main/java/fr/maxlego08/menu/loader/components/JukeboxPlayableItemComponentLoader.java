package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.JukeboxPlayableComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyJukeboxPlayableComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21")
public class JukeboxPlayableItemComponentLoader extends ItemComponentLoader {

    public JukeboxPlayableItemComponentLoader(){
        super("jukebox-playable");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableNamespacedKey songKey = this.asResolvableKey(configuration, path);
        if (songKey == null) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new JukeboxPlayableComponent(songKey)
                : new LegacyJukeboxPlayableComponent(songKey);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        JukeboxPlayable jukeboxPlayable = itemStack.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
        if (jukeboxPlayable == null) return null;
        NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG).getKey(jukeboxPlayable.jukeboxSong());
        return key == null ? null : new JukeboxPlayableComponent(ResolvableNamespacedKey.of(key));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasJukeboxPlayable()) return null;
        NamespacedKey key = itemMeta.getJukeboxPlayable().getSongKey();
        return key == null ? null : new LegacyJukeboxPlayableComponent(ResolvableNamespacedKey.of(key));
    }
}
