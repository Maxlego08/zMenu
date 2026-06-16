package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.function.Function;

public abstract class DyeColorVariantLoader extends ItemComponentLoader {
    private final Function<DyeColor, ItemComponent> componentFactory;

    protected DyeColorVariantLoader(String path, Function<DyeColor, ItemComponent> componentFactory) {
        super(path);
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        try {
            DyeColor dyeColor = DyeColor.valueOf(value.toUpperCase(Locale.ROOT));
            return componentFactory.apply(dyeColor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
