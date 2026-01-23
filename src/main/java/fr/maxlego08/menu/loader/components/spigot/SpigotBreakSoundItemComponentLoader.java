package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BreakSoundComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotBreakSoundItemComponentLoader extends ItemComponentLoader {

    public SpigotBreakSoundItemComponentLoader(){
        super("break-sound");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);

        String sound = configuration.getString(path);
        if (sound != null) {
            try {
                NamespacedKey key = NamespacedKey.fromString(sound.toLowerCase());
                if (key != null) {
                    return new BreakSoundComponent(Registry.SOUNDS.getOrThrow(key));
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }
}
