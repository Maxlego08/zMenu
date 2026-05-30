package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

public abstract class PaperRegistryVariantLoader<T extends Keyed> extends ItemComponentLoader {
    private final RegistryKey<T> registryKey;
    private final Function<T, ItemComponent> componentFactory;

    protected PaperRegistryVariantLoader(String path, RegistryKey<T> registryKey, Function<T, ItemComponent> componentFactory) {
        super(path);
        this.registryKey = registryKey;
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String value = configuration.getString(path);
        if (value == null) return null;
        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) return null;
        try {
            T registryValue = RegistryAccess.registryAccess().getRegistry(this.registryKey).getOrThrow(key);
            return componentFactory.apply(registryValue);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
