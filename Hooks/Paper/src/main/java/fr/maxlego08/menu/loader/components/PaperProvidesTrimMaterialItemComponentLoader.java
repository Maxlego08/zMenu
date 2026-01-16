package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.paper.components.PaperProvidesTrimMaterialComponent;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PaperProvidesTrimMaterialItemComponentLoader extends ItemComponentLoader {

    public PaperProvidesTrimMaterialItemComponentLoader(){
        super("provides_trim_material");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) return null;
        try {
            return new PaperProvidesTrimMaterialComponent(RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
