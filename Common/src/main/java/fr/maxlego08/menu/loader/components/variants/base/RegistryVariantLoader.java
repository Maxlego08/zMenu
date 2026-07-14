package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;

public abstract class RegistryVariantLoader<T extends Keyed> extends ItemComponentLoader {
    private final RegistryKey<T> registryKey;
    private final Function<ResolvableRegistryEntry<T>, ItemComponent> componentFactory;

    protected RegistryVariantLoader(@NotNull String path, @NotNull RegistryKey<T> registryKey, Function<ResolvableRegistryEntry<T>, ItemComponent> componentFactory) {
        super(path);
        this.registryKey = registryKey;
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        ResolvableRegistryEntry<T> resolvableRegistryEntry = ResolvableRegistry.autoOrNull(configuration.getString(path), this.registryKey);
        if (resolvableRegistryEntry == null) return null;
        return this.componentFactory.apply(resolvableRegistryEntry);
    }
}
