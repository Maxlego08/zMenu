package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.DyeColorComponent;
import fr.maxlego08.menu.loader.components.AbstractColorItemComponentLoader;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotDyeColorItemComponentLoader extends AbstractColorItemComponentLoader {

    public SpigotDyeColorItemComponentLoader(){
        super("dyed_color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);

        Object obj = configuration.get(path);
        if (obj == null) return null;
        Color color = parseColor(obj);
        if (color == null) return null;
        return new DyeColorComponent(color);
    }
}
