package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.NoteBlockSoundComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyNoteBlockSoundComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class NoteBlockSoundItemComponentLoader extends ItemComponentLoader {

    public NoteBlockSoundItemComponentLoader(){
        super("note-block-sound");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableNamespacedKey resolvableNamespacedKey = ResolvableNamespacedKey.autoOrNull(configuration.getString(path));
        if (resolvableNamespacedKey == null) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new NoteBlockSoundComponent(resolvableNamespacedKey)
                : new LegacyNoteBlockSoundComponent(resolvableNamespacedKey);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Key noteBlockSound = itemStack.getData(DataComponentTypes.NOTE_BLOCK_SOUND);
        if (noteBlockSound == null) return null;
        NamespacedKey key = NamespacedKey.fromString(noteBlockSound.asString());
        return key == null ? null : new NoteBlockSoundComponent(ResolvableNamespacedKey.of(key));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta skullMeta)) return null;
        NamespacedKey key = skullMeta.getNoteBlockSound();
        return key == null ? null : new LegacyNoteBlockSoundComponent(ResolvableNamespacedKey.of(key));
    }
}
