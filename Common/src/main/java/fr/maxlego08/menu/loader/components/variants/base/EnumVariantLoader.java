package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.function.Function;

public abstract class EnumVariantLoader<T extends Enum<T>> extends ItemComponentLoader {
    private final Class<T> enumClass;
    private final Function<T, ItemComponent> componentFactory;

    protected EnumVariantLoader(String path, Class<T> enumClass, Function<T, ItemComponent> componentFactory) {
        super(path);
        this.enumClass = enumClass;
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        try {
            T variant = Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
            return componentFactory.apply(variant);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
