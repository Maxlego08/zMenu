package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.PaperNoteBlockSoundComponent;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PaperNoteBlockSoundItemComponentLoader extends ItemComponentLoader {

    public PaperNoteBlockSoundItemComponentLoader(){
        super("note_block_sound");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String sound = configuration.getString(path);
        if (sound == null) return null;
        try{
            return new PaperNoteBlockSoundComponent(Key.key(sound));
        } catch (Exception e) {
            return null;
        }
    }
}
