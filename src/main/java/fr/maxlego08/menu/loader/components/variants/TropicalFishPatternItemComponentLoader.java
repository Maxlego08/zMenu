package fr.maxlego08.menu.loader.components.variants;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.variants.TropicalFishPatternComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.TropicalFish;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public final class TropicalFishPatternItemComponentLoader extends ItemComponentLoader {

    public TropicalFishPatternItemComponentLoader() {
        super("tropical-fish/pattern");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        ResolvableEnum<TropicalFish.Pattern> patternResolvableEnum = ResolvableEnum.autoOrNull(TropicalFish.Pattern.class, configuration.getString(path));
        return patternResolvableEnum != null ? new TropicalFishPatternComponent(patternResolvableEnum) : null;
    }
}
