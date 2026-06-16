package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.loader.components.AbstractColorItemComponentLoader;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.function.Function;

public abstract class CollarColorLoader extends AbstractColorItemComponentLoader {
    private final Function<DyeColor, ItemComponent> componentFactory;

    protected CollarColorLoader(String path, Function<DyeColor, ItemComponent> componentFactory) {
        super(path);
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        Object rawColor = configuration.get(path);
        if (rawColor == null) return null;
        Color color = parseColor(rawColor);
        DyeColor dyeColor;
        if (color == null) {
            try {
                dyeColor = DyeColor.valueOf(rawColor.toString().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else {
            dyeColor = DyeColor.getByColor(color);
        }
        if (dyeColor == null) return null;
        return componentFactory.apply(dyeColor);
    }
}
