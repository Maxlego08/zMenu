package fr.maxlego08.menu.loader.components.variants;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.variants.TropicalFishPatternColorComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class TropicalFishPatternColorItemComponentLoader extends ItemComponentLoader {

    public TropicalFishPatternColorItemComponentLoader() {
        super("tropical-fish/pattern-color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        ResolvableEnum<DyeColor> dyeColorResolvableEnum = ResolvableEnum.autoOrNull(DyeColor.class, configuration.getString(path));
        return dyeColorResolvableEnum != null ? new TropicalFishPatternColorComponent(dyeColorResolvableEnum) : null;
    }
}
