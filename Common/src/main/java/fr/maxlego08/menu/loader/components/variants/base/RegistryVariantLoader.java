package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.function.Function;

public abstract class RegistryVariantLoader<T extends Keyed> extends ItemComponentLoader {
    private final Registry<T> registry;
    private final Function<T, ItemComponent> componentFactory;

    protected RegistryVariantLoader(String path, Registry<T> registry, Function<T, ItemComponent> componentFactory) {
        super(path);
        this.registry = registry;
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        NamespacedKey key = NamespacedKey.fromString(value.toLowerCase(Locale.ROOT));
        if (key == null) return null;
        try {
            return componentFactory.apply(registry.getOrThrow(key));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
