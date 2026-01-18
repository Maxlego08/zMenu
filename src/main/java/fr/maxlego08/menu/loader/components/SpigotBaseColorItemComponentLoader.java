package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.BaseColorComponent;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotBaseColorItemComponentLoader extends ItemComponentLoader {

    public SpigotBaseColorItemComponentLoader(){
        super("base_color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        try {
            path = normalizePath(path);
            String string = configuration.getString(path);
            if (string == null) return null;
            DyeColor baseColor = DyeColor.valueOf(string.toUpperCase());
            return new BaseColorComponent(baseColor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
