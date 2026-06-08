package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BaseColorComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotBaseColorItemComponentLoader extends ItemComponentLoader {

    public SpigotBaseColorItemComponentLoader(){
        super("base-color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        try {
            path = this.normalizePath(path);
            String string = configuration.getString(path);
            if (string == null) return null;
            DyeColor baseColor = DyeColor.valueOf(string.toUpperCase(Locale.ROOT));
            return new BaseColorComponent(baseColor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
