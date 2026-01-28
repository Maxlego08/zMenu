package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MaxDamageComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotMaxDamageItemComponentLoader extends ItemComponentLoader {

    public SpigotMaxDamageItemComponentLoader(){
        super("max-damage");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        int maxDamage = configuration.getInt(path);
        return new MaxDamageComponent(maxDamage);
    }
}
