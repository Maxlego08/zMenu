package fr.maxlego08.menu;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("26.1")
public class DyeItemComponentLoader extends ItemComponentLoader {

    public DyeItemComponentLoader() {
        super("dye");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableEnum<DyeColor> dyeColorResolvableEnum = ResolvableEnum.autoOrNull(DyeColor.class, configuration.getString(path));
        return dyeColorResolvableEnum == null ? null : new DyeComponent(dyeColorResolvableEnum);
    }
}
