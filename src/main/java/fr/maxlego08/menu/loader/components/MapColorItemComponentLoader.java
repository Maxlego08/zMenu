package fr.maxlego08.menu.loader.components;

import fr.maxlego08.common.loader.components.AbstractColorItemComponentLoader;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.MapColorComponent;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MapColorItemComponentLoader extends AbstractColorItemComponentLoader {

    public MapColorItemComponentLoader(){
        super("map_color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        Object o = configuration.get(path);
        if (o == null) return null;
        Color color = parseColor(o);
        return color == null ? null : new MapColorComponent(color);
    }
}
