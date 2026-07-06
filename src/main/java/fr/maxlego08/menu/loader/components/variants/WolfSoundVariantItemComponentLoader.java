package fr.maxlego08.menu.loader.components.variants;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.variants.WolfSoundVariantComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Wolf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class WolfSoundVariantItemComponentLoader extends ItemComponentLoader {

    public WolfSoundVariantItemComponentLoader() {
        super("wolf/sound-variant");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableRegistryEntry<Wolf.SoundVariant> soundVariantResolvableRegistryEntry = ResolvableRegistry.autoOrNull(configuration.getString(path), Wolf.SoundVariant.class);
        return soundVariantResolvableRegistryEntry != null ? new WolfSoundVariantComponent(soundVariantResolvableRegistryEntry) : null;
    }
}
