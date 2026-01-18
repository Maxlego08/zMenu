package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.ItemModelComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotItemModelItemComponentLoader extends ItemComponentLoader {

    public SpigotItemModelItemComponentLoader(){
        super("item_model");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String modelIdStr = configuration.getString(path);
        if (modelIdStr == null) return null;
        NamespacedKey modelId = NamespacedKey.fromString(modelIdStr);
        return modelId == null ? null : new ItemModelComponent(modelId);
    }
}
