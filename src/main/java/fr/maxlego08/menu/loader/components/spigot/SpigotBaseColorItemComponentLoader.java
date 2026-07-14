package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BaseColorComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.SimpleResolvable;
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
        path = this.normalizePath(path);
        String string = configuration.getString(path);
        if (string == null) return null;
        Resolvable<DyeColor> dyeColorResolvable;
        try {
            DyeColor baseColor = DyeColor.valueOf(string.toUpperCase(Locale.ROOT));
            dyeColorResolvable = SimpleResolvable.of(baseColor, DyeColor::valueOf);
        } catch (IllegalArgumentException e) {
            dyeColorResolvable = SimpleResolvable.ofExpression(string, s -> {
                String normalized = s.trim()
                        .replace(" ", "_")
                        .replace("-", "_")
                        .toUpperCase(Locale.ROOT);
                return Enum.valueOf(DyeColor.class, normalized);
            });
        }

        return new BaseColorComponent(dyeColorResolvable);
    }
}
