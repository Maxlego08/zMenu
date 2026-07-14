package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import fr.maxlego08.menu.loader.components.AbstractColorItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

public abstract class DyeColorLoader extends AbstractColorItemComponentLoader {
    private final Function<ResolvableDyeColor, ItemComponent> componentFactory;

    protected DyeColorLoader(String path, Function<ResolvableDyeColor, ItemComponent> componentFactory) {
        super(path);
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableDyeColor resolvableDyeColor = ResolvableDyeColor.autoOrNull(configuration.get(path));
        if (resolvableDyeColor == null) return null;
        return this.componentFactory.apply(resolvableDyeColor);
    }
}
